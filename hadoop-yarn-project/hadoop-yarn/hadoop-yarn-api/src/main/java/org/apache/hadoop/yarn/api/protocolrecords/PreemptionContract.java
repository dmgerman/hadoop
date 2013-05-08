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
name|java
operator|.
name|util
operator|.
name|Set
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_comment
comment|/**  * Description of resources requested back by the<code>ResourceManager</code>.  * The<code>ApplicationMaster</code> (AM) can satisfy this request according  * to its own priorities to prevent containers from being forcibly killed by  * the platform.  * @see PreemptionMessage  */
end_comment

begin_interface
DECL|interface|PreemptionContract
specifier|public
interface|interface
name|PreemptionContract
block|{
comment|/**    * If the AM releases resources matching these requests, then the {@link    * PreemptionContainer}s enumerated in {@link #getContainers()} should not be    * evicted from the cluster. Due to delays in propagating cluster state and    * sending these messages, there are conditions where satisfied contracts may    * not prevent the platform from killing containers.    * @return List of {@link PreemptionResourceRequest} to update the    *<code>ApplicationMaster</code> about resources requested back by the    *<code>ResourceManager</code>.    * @see AllocateRequest#setAskList(List)    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getResourceRequest ()
specifier|public
name|List
argument_list|<
name|PreemptionResourceRequest
argument_list|>
name|getResourceRequest
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setResourceRequest (List<PreemptionResourceRequest> req)
specifier|public
name|void
name|setResourceRequest
parameter_list|(
name|List
argument_list|<
name|PreemptionResourceRequest
argument_list|>
name|req
parameter_list|)
function_decl|;
comment|/**    * Assign the set of {@link PreemptionContainer} specifying which containers    * owned by the<code>ApplicationMaster</code> that may be reclaimed by the    *<code>ResourceManager</code>. If the AM prefers a different set of    * containers, then it may checkpoint or kill containers matching the    * description in {@link #getResourceRequest}.    * @return Set of containers at risk if the contract is not met.    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getContainers ()
specifier|public
name|Set
argument_list|<
name|PreemptionContainer
argument_list|>
name|getContainers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setContainers (Set<PreemptionContainer> containers)
specifier|public
name|void
name|setContainers
parameter_list|(
name|Set
argument_list|<
name|PreemptionContainer
argument_list|>
name|containers
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

