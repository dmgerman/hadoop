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
import|import static
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
name|*
import|;
end_import

begin_import
import|import static
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
name|*
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
name|NodeAttribute
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
name|Set
import|;
end_import

begin_comment
comment|/**  *<p>  * The response sent by the<code>ResourceManager</code> to a client requesting  * a node attributes in cluster.  *</p>  *  * @see ApplicationClientProtocol#getClusterNodeAttributes  * (GetClusterNodeAttributesRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|GetClusterNodeAttributesResponse
specifier|public
specifier|abstract
class|class
name|GetClusterNodeAttributesResponse
block|{
comment|/**    * Create instance of GetClusterNodeAttributesResponse.    *    * @param attributes    * @return GetClusterNodeAttributesResponse.    */
DECL|method|newInstance ( Set<NodeAttribute> attributes)
specifier|public
specifier|static
name|GetClusterNodeAttributesResponse
name|newInstance
parameter_list|(
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|attributes
parameter_list|)
block|{
name|GetClusterNodeAttributesResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetClusterNodeAttributesResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setNodeAttributes
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Set node attributes to the response.    *    * @param attributes Node attributes    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setNodeAttributes (Set<NodeAttribute> attributes)
specifier|public
specifier|abstract
name|void
name|setNodeAttributes
parameter_list|(
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|attributes
parameter_list|)
function_decl|;
comment|/**    * Get node attributes of the response.    *    * @return Node attributes    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getNodeAttributes ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|getNodeAttributes
parameter_list|()
function_decl|;
block|}
end_class

end_unit

