begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.nodelabels
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
name|resourcemanager
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|NodeLabel
import|;
end_import

begin_comment
comment|/**  * Node labels utilities.  */
end_comment

begin_class
DECL|class|NodeLabelsUtils
specifier|public
specifier|final
class|class
name|NodeLabelsUtils
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|NodeLabelsUtils
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|NodeLabelsUtils ()
specifier|private
name|NodeLabelsUtils
parameter_list|()
block|{
comment|/* Hidden constructor */
block|}
DECL|method|convertToStringSet (Set<NodeLabel> nodeLabels)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|convertToStringSet
parameter_list|(
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|nodeLabels
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|nodeLabels
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|labels
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeLabel
name|label
range|:
name|nodeLabels
control|)
block|{
name|labels
operator|.
name|add
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|labels
return|;
block|}
DECL|method|verifyCentralizedNodeLabelConfEnabled (String operation, boolean isCentralizedNodeLabelConfiguration)
specifier|public
specifier|static
name|void
name|verifyCentralizedNodeLabelConfEnabled
parameter_list|(
name|String
name|operation
parameter_list|,
name|boolean
name|isCentralizedNodeLabelConfiguration
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isCentralizedNodeLabelConfiguration
condition|)
block|{
name|String
name|msg
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Error when invoke method=%s because "
operator|+
literal|"centralized node label configuration is not enabled."
argument_list|,
name|operation
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns a set of node attributes whose name exists in the provided    *<code>attributeNames</code> list.    *    * @param attributeNames For this given list of attribute names get the    *          cluster NodeAttributes    * @param clusterNodeAttributes set of node Attributes    * @return set of Node Attributes which maps to the give attributes names    */
DECL|method|getNodeAttributesByName ( Set<String> attributeNames, Set<NodeAttribute> clusterNodeAttributes)
specifier|public
specifier|static
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|getNodeAttributesByName
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|attributeNames
parameter_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
name|clusterNodeAttributes
parameter_list|)
block|{
return|return
name|clusterNodeAttributes
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|attribute
lambda|->
name|attributeNames
operator|.
name|contains
argument_list|(
name|attribute
operator|.
name|getAttributeKey
argument_list|()
operator|.
name|getAttributeName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

