begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
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
name|hdds
operator|.
name|server
operator|.
name|ServiceRuntimeInfo
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
comment|/**  *  * This is the JMX management interface for scm information.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|SCMMXBean
specifier|public
interface|interface
name|SCMMXBean
extends|extends
name|ServiceRuntimeInfo
block|{
comment|/**    * Get the SCM RPC server port that used to listen to datanode requests.    * @return SCM datanode RPC server port    */
DECL|method|getDatanodeRpcPort ()
name|String
name|getDatanodeRpcPort
parameter_list|()
function_decl|;
comment|/**    * Get the SCM RPC server port that used to listen to client requests.    * @return SCM client RPC server port    */
DECL|method|getClientRpcPort ()
name|String
name|getClientRpcPort
parameter_list|()
function_decl|;
comment|/**    * Get container report info that includes container IO stats of nodes.    * @return The datanodeUUid to report json string mapping    */
DECL|method|getContainerReport ()
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getContainerReport
parameter_list|()
function_decl|;
comment|/**    * Returns chill mode status.    * @return boolean    */
DECL|method|isInChillMode ()
name|boolean
name|isInChillMode
parameter_list|()
function_decl|;
comment|/**    * Returns live chill mode container threshold.    * @return String    */
DECL|method|getChillModeCurrentContainerThreshold ()
name|double
name|getChillModeCurrentContainerThreshold
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

