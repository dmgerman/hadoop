begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
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

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"nodeLabelsInfo"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|NodeLabelsInfo
specifier|public
class|class
name|NodeLabelsInfo
block|{
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"nodeLabelInfo"
argument_list|)
DECL|field|nodeLabelsInfo
specifier|private
name|ArrayList
argument_list|<
name|NodeLabelInfo
argument_list|>
name|nodeLabelsInfo
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeLabelInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|NodeLabelsInfo ()
specifier|public
name|NodeLabelsInfo
parameter_list|()
block|{
comment|// JAXB needs this
block|}
DECL|method|NodeLabelsInfo (ArrayList<NodeLabelInfo> nodeLabels)
specifier|public
name|NodeLabelsInfo
parameter_list|(
name|ArrayList
argument_list|<
name|NodeLabelInfo
argument_list|>
name|nodeLabels
parameter_list|)
block|{
name|this
operator|.
name|nodeLabelsInfo
operator|=
name|nodeLabels
expr_stmt|;
block|}
DECL|method|NodeLabelsInfo (List<NodeLabel> nodeLabels)
specifier|public
name|NodeLabelsInfo
parameter_list|(
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|nodeLabels
parameter_list|)
block|{
name|this
operator|.
name|nodeLabelsInfo
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeLabelInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeLabel
name|label
range|:
name|nodeLabels
control|)
block|{
name|this
operator|.
name|nodeLabelsInfo
operator|.
name|add
argument_list|(
operator|new
name|NodeLabelInfo
argument_list|(
name|label
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|NodeLabelsInfo (Set<String> nodeLabelsName)
specifier|public
name|NodeLabelsInfo
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodeLabelsName
parameter_list|)
block|{
name|this
operator|.
name|nodeLabelsInfo
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeLabelInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|labelName
range|:
name|nodeLabelsName
control|)
block|{
name|this
operator|.
name|nodeLabelsInfo
operator|.
name|add
argument_list|(
operator|new
name|NodeLabelInfo
argument_list|(
name|labelName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getNodeLabelsInfo ()
specifier|public
name|ArrayList
argument_list|<
name|NodeLabelInfo
argument_list|>
name|getNodeLabelsInfo
parameter_list|()
block|{
return|return
name|nodeLabelsInfo
return|;
block|}
DECL|method|getNodeLabels ()
specifier|public
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|getNodeLabels
parameter_list|()
block|{
name|Set
argument_list|<
name|NodeLabel
argument_list|>
name|nodeLabels
init|=
operator|new
name|HashSet
argument_list|<
name|NodeLabel
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeLabelInfo
name|label
range|:
name|nodeLabelsInfo
control|)
block|{
name|nodeLabels
operator|.
name|add
argument_list|(
name|NodeLabel
operator|.
name|newInstance
argument_list|(
name|label
operator|.
name|getName
argument_list|()
argument_list|,
name|label
operator|.
name|getExclusivity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeLabels
return|;
block|}
DECL|method|getNodeLabelsName ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getNodeLabelsName
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|nodeLabelsName
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeLabelInfo
name|label
range|:
name|nodeLabelsInfo
control|)
block|{
name|nodeLabelsName
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
name|nodeLabelsName
return|;
block|}
DECL|method|setNodeLabelsInfo (ArrayList<NodeLabelInfo> nodeLabelInfo)
specifier|public
name|void
name|setNodeLabelsInfo
parameter_list|(
name|ArrayList
argument_list|<
name|NodeLabelInfo
argument_list|>
name|nodeLabelInfo
parameter_list|)
block|{
name|this
operator|.
name|nodeLabelsInfo
operator|=
name|nodeLabelInfo
expr_stmt|;
block|}
block|}
end_class

end_unit

