begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|service
operator|.
name|AbstractService
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|NodeToAttributes
import|;
end_import

begin_comment
comment|/**  * This class captures all interactions for Attributes with RM.  */
end_comment

begin_class
DECL|class|NodeAttributesManager
specifier|public
specifier|abstract
class|class
name|NodeAttributesManager
extends|extends
name|AbstractService
block|{
DECL|method|NodeAttributesManager (String name)
specifier|public
name|NodeAttributesManager
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * To completely replace the mappings for a given node with the new Set of    * Attributes which are under a given prefix. If the mapping contains an    * attribute whose type does not match a previously existing Attribute    * under the same prefix (name space) then exception is thrown.    * Key would be name of the node and value would be set of Attributes to    * be mapped. If the prefix is null, then all node attributes will be    * replaced regardless of what prefix they have.    *    * @param prefix node attribute prefix    * @param nodeAttributeMapping host name to a set of node attributes mapping    * @throws IOException if failed to replace attributes    */
DECL|method|replaceNodeAttributes (String prefix, Map<String, Set<NodeAttribute>> nodeAttributeMapping)
specifier|public
specifier|abstract
name|void
name|replaceNodeAttributes
parameter_list|(
name|String
name|prefix
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|nodeAttributeMapping
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * It adds or updates the attribute mapping for a given node with out    * impacting other existing attribute mapping. Key would be name of the node    * and value would be set of Attributes to be mapped.    *    * @param nodeAttributeMapping    * @throws IOException    */
DECL|method|addNodeAttributes ( Map<String, Set<NodeAttribute>> nodeAttributeMapping)
specifier|public
specifier|abstract
name|void
name|addNodeAttributes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|nodeAttributeMapping
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * It removes the specified attribute mapping for a given node with out    * impacting other existing attribute mapping. Key would be name of the node    * and value would be set of Attributes to be removed.    *    * @param nodeAttributeMapping    * @throws IOException    */
DECL|method|removeNodeAttributes ( Map<String, Set<NodeAttribute>> nodeAttributeMapping)
specifier|public
specifier|abstract
name|void
name|removeNodeAttributes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|nodeAttributeMapping
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a set of node attributes whose prefix is one of the given    * prefixes; if the prefix set is null or empty, all attributes are returned;    * if prefix set is given but no mapping could be found, an empty set    * is returned.    *    * @param prefix set of prefix string's for which the attributes needs to    *          returned    * @return Set of node Attributes    */
DECL|method|getClusterNodeAttributes ( Set<String> prefix)
specifier|public
specifier|abstract
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|getClusterNodeAttributes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|prefix
parameter_list|)
function_decl|;
comment|/**    * Return a map of Nodes to attribute value for the given NodeAttributeKeys.    * If the attributeKeys set is null or empty, then mapping for all attributes    * are returned.    *    * @return a Map of attributeKeys to a map of hostnames to its attribute    *         values.    */
DECL|method|getAttributesToNodes ( Set<NodeAttributeKey> attributes)
specifier|public
specifier|abstract
name|Map
argument_list|<
name|NodeAttributeKey
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|AttributeValue
argument_list|>
argument_list|>
name|getAttributesToNodes
parameter_list|(
name|Set
argument_list|<
name|NodeAttributeKey
argument_list|>
name|attributes
parameter_list|)
function_decl|;
comment|/**    * NodeAttribute to AttributeValue Map.    *    * @return Map<NodeAttribute, AttributeValue> mapping of Attribute to Value.    */
DECL|method|getAttributesForNode ( String hostName)
specifier|public
specifier|abstract
name|Map
argument_list|<
name|NodeAttribute
argument_list|,
name|AttributeValue
argument_list|>
name|getAttributesForNode
parameter_list|(
name|String
name|hostName
parameter_list|)
function_decl|;
comment|/**    * Get All node to Attributes list based on filter.    *    * @return List<NodeToAttributes> nodeToAttributes matching filter.If empty    * or null is passed as argument will return all.    */
DECL|method|getNodeToAttributes ( Set<String> prefix)
specifier|public
specifier|abstract
name|List
argument_list|<
name|NodeToAttributes
argument_list|>
name|getNodeToAttributes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|prefix
parameter_list|)
function_decl|;
comment|/**    * Get all node to Attributes mapping.    *    * @return Map<String, Set<NodeAttribute>> nodesToAttributes matching    * filter.If empty or null is passed as argument will return all.    */
DECL|method|getNodesToAttributes ( Set<String> hostNames)
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|getNodesToAttributes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|hostNames
parameter_list|)
function_decl|;
comment|// futuristic
comment|// public set<NodeId> getNodesMatchingExpression(String nodeLabelExp);
block|}
end_class

end_unit

