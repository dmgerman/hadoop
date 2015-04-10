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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p><code>YarnClusterMetrics</code> represents cluster metrics.</p>  *   *<p>Currently only number of<code>NodeManager</code>s is provided.</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|YarnClusterMetrics
specifier|public
specifier|abstract
class|class
name|YarnClusterMetrics
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (int numNodeManagers)
specifier|public
specifier|static
name|YarnClusterMetrics
name|newInstance
parameter_list|(
name|int
name|numNodeManagers
parameter_list|)
block|{
name|YarnClusterMetrics
name|metrics
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|YarnClusterMetrics
operator|.
name|class
argument_list|)
decl_stmt|;
name|metrics
operator|.
name|setNumNodeManagers
argument_list|(
name|numNodeManagers
argument_list|)
expr_stmt|;
return|return
name|metrics
return|;
block|}
comment|/**    * Get the number of<code>NodeManager</code>s in the cluster.    * @return number of<code>NodeManager</code>s in the cluster    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumNodeManagers ()
specifier|public
specifier|abstract
name|int
name|getNumNodeManagers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumNodeManagers (int numNodeManagers)
specifier|public
specifier|abstract
name|void
name|setNumNodeManagers
parameter_list|(
name|int
name|numNodeManagers
parameter_list|)
function_decl|;
comment|/**    * Get the number of<code>DecommissionedNodeManager</code>s in the cluster.    *     * @return number of<code>DecommissionedNodeManager</code>s in the cluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getNumDecommissionedNodeManagers ()
specifier|public
specifier|abstract
name|int
name|getNumDecommissionedNodeManagers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumDecommissionedNodeManagers ( int numDecommissionedNodeManagers)
specifier|public
specifier|abstract
name|void
name|setNumDecommissionedNodeManagers
parameter_list|(
name|int
name|numDecommissionedNodeManagers
parameter_list|)
function_decl|;
comment|/**    * Get the number of<code>ActiveNodeManager</code>s in the cluster.    *     * @return number of<code>ActiveNodeManager</code>s in the cluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getNumActiveNodeManagers ()
specifier|public
specifier|abstract
name|int
name|getNumActiveNodeManagers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumActiveNodeManagers (int numActiveNodeManagers)
specifier|public
specifier|abstract
name|void
name|setNumActiveNodeManagers
parameter_list|(
name|int
name|numActiveNodeManagers
parameter_list|)
function_decl|;
comment|/**    * Get the number of<code>LostNodeManager</code>s in the cluster.    *     * @return number of<code>LostNodeManager</code>s in the cluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getNumLostNodeManagers ()
specifier|public
specifier|abstract
name|int
name|getNumLostNodeManagers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumLostNodeManagers (int numLostNodeManagers)
specifier|public
specifier|abstract
name|void
name|setNumLostNodeManagers
parameter_list|(
name|int
name|numLostNodeManagers
parameter_list|)
function_decl|;
comment|/**    * Get the number of<code>UnhealthyNodeManager</code>s in the cluster.    *     * @return number of<code>UnhealthyNodeManager</code>s in the cluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getNumUnhealthyNodeManagers ()
specifier|public
specifier|abstract
name|int
name|getNumUnhealthyNodeManagers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumUnhealthyNodeManagers (int numUnhealthNodeManagers)
specifier|public
specifier|abstract
name|void
name|setNumUnhealthyNodeManagers
parameter_list|(
name|int
name|numUnhealthNodeManagers
parameter_list|)
function_decl|;
comment|/**    * Get the number of<code>RebootedNodeManager</code>s in the cluster.    *     * @return number of<code>RebootedNodeManager</code>s in the cluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getNumRebootedNodeManagers ()
specifier|public
specifier|abstract
name|int
name|getNumRebootedNodeManagers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumRebootedNodeManagers (int numRebootedNodeManagers)
specifier|public
specifier|abstract
name|void
name|setNumRebootedNodeManagers
parameter_list|(
name|int
name|numRebootedNodeManagers
parameter_list|)
function_decl|;
block|}
end_class

end_unit

