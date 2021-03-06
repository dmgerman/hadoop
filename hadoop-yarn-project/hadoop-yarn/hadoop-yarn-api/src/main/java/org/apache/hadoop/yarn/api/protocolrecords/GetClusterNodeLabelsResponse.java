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
name|ArrayList
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
name|records
operator|.
name|NodeLabel
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

begin_class
annotation|@
name|Public
annotation|@
name|Evolving
DECL|class|GetClusterNodeLabelsResponse
specifier|public
specifier|abstract
class|class
name|GetClusterNodeLabelsResponse
block|{
comment|/**    * Creates a new instance.    *    * @param labels Node labels    * @return response    * @deprecated Use {@link #newInstance(List)} instead.    */
annotation|@
name|Deprecated
DECL|method|newInstance (Set<String> labels)
specifier|public
specifier|static
name|GetClusterNodeLabelsResponse
name|newInstance
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
block|{
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|label
range|:
name|labels
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newInstance
argument_list|(
name|list
argument_list|)
return|;
block|}
DECL|method|newInstance (List<NodeLabel> labels)
specifier|public
specifier|static
name|GetClusterNodeLabelsResponse
name|newInstance
parameter_list|(
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|labels
parameter_list|)
block|{
name|GetClusterNodeLabelsResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|GetClusterNodeLabelsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setNodeLabelList
argument_list|(
name|labels
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|setNodeLabelList (List<NodeLabel> labels)
specifier|public
specifier|abstract
name|void
name|setNodeLabelList
parameter_list|(
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|labels
parameter_list|)
function_decl|;
DECL|method|getNodeLabelList ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|getNodeLabelList
parameter_list|()
function_decl|;
comment|/**    * Set node labels to the response.    *    * @param labels Node labels    * @deprecated Use {@link #setNodeLabelList(List)} instead.    */
annotation|@
name|Deprecated
DECL|method|setNodeLabels (Set<String> labels)
specifier|public
specifier|abstract
name|void
name|setNodeLabels
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
function_decl|;
comment|/**    * Get node labels of the response.    *    * @return Node labels    * @deprecated Use {@link #getNodeLabelList()} instead.    */
annotation|@
name|Deprecated
DECL|method|getNodeLabels ()
specifier|public
specifier|abstract
name|Set
argument_list|<
name|String
argument_list|>
name|getNodeLabels
parameter_list|()
function_decl|;
block|}
end_class

end_unit

