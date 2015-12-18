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
name|Priority
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
comment|/**  *<p>  * The response sent by the<code>ResourceManager</code> to the client on update  * the application priority.  *</p>  *<p>  * A response without exception means that the move has completed successfully.  *</p>  *   * @see ApplicationClientProtocol#updateApplicationPriority(UpdateApplicationPriorityRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|UpdateApplicationPriorityResponse
specifier|public
specifier|abstract
class|class
name|UpdateApplicationPriorityResponse
block|{
DECL|method|newInstance ( Priority priority)
specifier|public
specifier|static
name|UpdateApplicationPriorityResponse
name|newInstance
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|UpdateApplicationPriorityResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|UpdateApplicationPriorityResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setApplicationPriority
argument_list|(
name|priority
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get the<code>Priority</code> of the application to be set.    * @return Updated<code>Priority</code> of the application.    */
DECL|method|getApplicationPriority ()
specifier|public
specifier|abstract
name|Priority
name|getApplicationPriority
parameter_list|()
function_decl|;
comment|/**    * Set the<code>Priority</code> of the application.    *    * @param priority<code>Priority</code> of the application    */
DECL|method|setApplicationPriority (Priority priority)
specifier|public
specifier|abstract
name|void
name|setApplicationPriority
parameter_list|(
name|Priority
name|priority
parameter_list|)
function_decl|;
block|}
end_class

end_unit

