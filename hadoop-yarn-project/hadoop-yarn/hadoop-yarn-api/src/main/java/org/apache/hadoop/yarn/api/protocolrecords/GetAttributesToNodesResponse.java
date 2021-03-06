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
name|Map
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
name|NodeAttributeKey
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
name|NodeToAttributeValue
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
comment|/**  *<p>  * The response sent by the<code>ResourceManager</code> to a client requesting  * node to attribute value mapping for all or given set of Node AttributeKey's.  *</p>  *  * @see ApplicationClientProtocol#getAttributesToNodes  *      (GetAttributesToNodesRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|GetAttributesToNodesResponse
specifier|public
specifier|abstract
class|class
name|GetAttributesToNodesResponse
block|{
DECL|method|newInstance ( Map<NodeAttributeKey, List<NodeToAttributeValue>> map)
specifier|public
specifier|static
name|GetAttributesToNodesResponse
name|newInstance
parameter_list|(
name|Map
argument_list|<
name|NodeAttributeKey
argument_list|,
name|List
argument_list|<
name|NodeToAttributeValue
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
name|GetAttributesToNodesResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetAttributesToNodesResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setAttributeToNodes
argument_list|(
name|map
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Public
annotation|@
name|Evolving
DECL|method|setAttributeToNodes ( Map<NodeAttributeKey, List<NodeToAttributeValue>> map)
specifier|public
specifier|abstract
name|void
name|setAttributeToNodes
parameter_list|(
name|Map
argument_list|<
name|NodeAttributeKey
argument_list|,
name|List
argument_list|<
name|NodeToAttributeValue
argument_list|>
argument_list|>
name|map
parameter_list|)
function_decl|;
comment|/**    * Get mapping of NodeAttributeKey to its associated mapping of list of    * NodeToAttributeValue associated with attribute.    *    * @return Map of node attributes to list of NodeToAttributeValue.    */
annotation|@
name|Public
annotation|@
name|Evolving
specifier|public
specifier|abstract
name|Map
argument_list|<
name|NodeAttributeKey
argument_list|,
DECL|method|getAttributesToNodes ()
name|List
argument_list|<
name|NodeToAttributeValue
argument_list|>
argument_list|>
name|getAttributesToNodes
parameter_list|()
function_decl|;
block|}
end_class

end_unit

