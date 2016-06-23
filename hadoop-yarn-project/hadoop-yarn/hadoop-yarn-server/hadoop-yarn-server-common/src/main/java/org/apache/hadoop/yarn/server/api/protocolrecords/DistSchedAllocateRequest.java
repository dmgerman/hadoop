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
name|protocolrecords
operator|.
name|AllocateRequest
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
name|Container
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
comment|/**  * Request for a distributed scheduler to notify allocation of containers to  * the Resource Manager.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|DistSchedAllocateRequest
specifier|public
specifier|abstract
class|class
name|DistSchedAllocateRequest
block|{
comment|/**    * Get the underlying<code>AllocateRequest</code> object.    * @return Allocate request    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getAllocateRequest ()
specifier|public
specifier|abstract
name|AllocateRequest
name|getAllocateRequest
parameter_list|()
function_decl|;
comment|/**    * Set the underlying<code>AllocateRequest</code> object.    * @param allocateRequest  Allocate request    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setAllocateRequest (AllocateRequest allocateRequest)
specifier|public
specifier|abstract
name|void
name|setAllocateRequest
parameter_list|(
name|AllocateRequest
name|allocateRequest
parameter_list|)
function_decl|;
comment|/**    * Get the list of<em>newly allocated</em><code>Container</code> by the    * Distributed Scheduling component on the NodeManager.    * @return list of<em>newly allocated</em><code>Container</code>    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|getAllocatedContainers ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|Container
argument_list|>
name|getAllocatedContainers
parameter_list|()
function_decl|;
comment|/**    * Set the list of<em>newly allocated</em><code>Container</code> by the    * Distributed Scheduling component on the NodeManager.    * @param containers list of<em>newly allocated</em><code>Container</code>    */
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setAllocatedContainers (List<Container> containers)
specifier|public
specifier|abstract
name|void
name|setAllocatedContainers
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
function_decl|;
block|}
end_class

end_unit

