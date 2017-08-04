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
name|ContainerManagementProtocol
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
name|NMToken
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
name|Token
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *<p>The request sent by<code>Application Master</code> to the  *<code>Node Manager</code> to change the resource quota of a container.</p>  *  * @see ContainerManagementProtocol#updateContainer(ContainerUpdateRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ContainerUpdateRequest
specifier|public
specifier|abstract
class|class
name|ContainerUpdateRequest
block|{
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|newInstance ( List<Token> containersToIncrease)
specifier|public
specifier|static
name|ContainerUpdateRequest
name|newInstance
parameter_list|(
name|List
argument_list|<
name|Token
argument_list|>
name|containersToIncrease
parameter_list|)
block|{
name|ContainerUpdateRequest
name|request
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerUpdateRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setContainersToUpdate
argument_list|(
name|containersToIncrease
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
comment|/**    * Get a list of container tokens to be used for authorization during    * container resource update.    *<p>    * Note: {@link NMToken} will be used for authenticating communication with    * {@code NodeManager}.    * @return the list of container tokens to be used for authorization during    * container resource update.    * @see NMToken    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getContainersToUpdate ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|Token
argument_list|>
name|getContainersToUpdate
parameter_list|()
function_decl|;
comment|/**    * Set container tokens to be used during container resource increase.    * The token is acquired from    *<code>AllocateResponse.getUpdatedContainers</code>.    * The token contains the container id and resource capability required for    * container resource update.    * @param containersToUpdate the list of container tokens to be used    *                             for container resource increase.    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setContainersToUpdate ( List<Token> containersToUpdate)
specifier|public
specifier|abstract
name|void
name|setContainersToUpdate
parameter_list|(
name|List
argument_list|<
name|Token
argument_list|>
name|containersToUpdate
parameter_list|)
function_decl|;
block|}
end_class

end_unit

