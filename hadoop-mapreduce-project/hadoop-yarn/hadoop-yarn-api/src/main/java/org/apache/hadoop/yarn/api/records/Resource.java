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
name|yarn
operator|.
name|api
operator|.
name|AMRMProtocol
import|;
end_import

begin_comment
comment|/**  *<p><code>Resource</code> models a set of computer resources in the   * cluster.</p>  *   *<p>Currrently it only models<em>memory</em>.</p>  *   *<p>Typically, applications request<code>Resource</code> of suitable  * capability to run their component tasks.</p>  *   * @see ResourceRequest  * @see AMRMProtocol#allocate(org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest)  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|Resource
specifier|public
interface|interface
name|Resource
extends|extends
name|Comparable
argument_list|<
name|Resource
argument_list|>
block|{
comment|/**    * Get<em>memory</em> of the resource.    * @return<em>memory</em> of the resource    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getMemory ()
specifier|public
specifier|abstract
name|int
name|getMemory
parameter_list|()
function_decl|;
comment|/**    * Set<em>memory</em> of the resource.    * @param memory<em>memory</em> of the resource    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setMemory (int memory)
specifier|public
specifier|abstract
name|void
name|setMemory
parameter_list|(
name|int
name|memory
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

