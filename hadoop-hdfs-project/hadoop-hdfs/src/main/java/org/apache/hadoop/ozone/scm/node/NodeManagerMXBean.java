begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.node
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|node
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  * This is the JMX management interface for node manager information.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|NodeManagerMXBean
specifier|public
interface|interface
name|NodeManagerMXBean
block|{
comment|/**    * Get the minimum number of nodes to get out of chill mode.    *    * @return int    */
DECL|method|getMinimumChillModeNodes ()
name|int
name|getMinimumChillModeNodes
parameter_list|()
function_decl|;
comment|/**    * Returns a chill mode status string.    * @return String    */
DECL|method|getChillModeStatus ()
name|String
name|getChillModeStatus
parameter_list|()
function_decl|;
comment|/**    * Returns true if node manager is out of chill mode, else false.    * @return true if out of chill mode, else false    */
DECL|method|isOutOfChillMode ()
name|boolean
name|isOutOfChillMode
parameter_list|()
function_decl|;
comment|/**    * Get the number of data nodes that in all states.    *    * @return A state to number of nodes that in this state mapping    */
DECL|method|getNodeCount ()
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getNodeCount
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

