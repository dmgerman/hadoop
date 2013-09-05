begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
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
name|resourcemanager
operator|.
name|scheduler
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
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_comment
comment|/**  * Represents a YARN Cluster Node from the viewpoint of the scheduler.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SchedulerNode
specifier|public
specifier|abstract
class|class
name|SchedulerNode
block|{
comment|/**    * Get the name of the node for scheduling matching decisions.    *<p/>    * Typically this is the 'hostname' reported by the node, but it could be     * configured to be 'hostname:port' reported by the node via the     * {@link YarnConfiguration#RM_SCHEDULER_INCLUDE_PORT_IN_NODE_NAME} constant.    * The main usecase of this is Yarn minicluster to be able to differentiate    * node manager instances by their port number.    *     * @return name of the node for scheduling matching decisions.    */
DECL|method|getNodeName ()
specifier|public
specifier|abstract
name|String
name|getNodeName
parameter_list|()
function_decl|;
comment|/**    * Get rackname.    * @return rackname    */
DECL|method|getRackName ()
specifier|public
specifier|abstract
name|String
name|getRackName
parameter_list|()
function_decl|;
comment|/**    * Get used resources on the node.    * @return used resources on the node    */
DECL|method|getUsedResource ()
specifier|public
specifier|abstract
name|Resource
name|getUsedResource
parameter_list|()
function_decl|;
comment|/**    * Get available resources on the node.    * @return available resources on the node    */
DECL|method|getAvailableResource ()
specifier|public
specifier|abstract
name|Resource
name|getAvailableResource
parameter_list|()
function_decl|;
comment|/**    * Get number of active containers on the node.    * @return number of active containers on the node    */
DECL|method|getNumContainers ()
specifier|public
specifier|abstract
name|int
name|getNumContainers
parameter_list|()
function_decl|;
comment|/**    * Get total resources on the node.    * @return total resources on the node.    */
DECL|method|getTotalResource ()
specifier|public
specifier|abstract
name|Resource
name|getTotalResource
parameter_list|()
function_decl|;
block|}
end_class

end_unit

