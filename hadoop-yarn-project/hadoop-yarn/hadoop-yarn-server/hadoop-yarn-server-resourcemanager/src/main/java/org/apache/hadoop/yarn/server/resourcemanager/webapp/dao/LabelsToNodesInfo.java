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
name|HashMap
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
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|NodeIDsInfo
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"labelsToNodesInfo"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|LabelsToNodesInfo
specifier|public
class|class
name|LabelsToNodesInfo
block|{
DECL|field|labelsToNodes
specifier|protected
name|Map
argument_list|<
name|NodeLabelInfo
argument_list|,
name|NodeIDsInfo
argument_list|>
name|labelsToNodes
init|=
operator|new
name|HashMap
argument_list|<
name|NodeLabelInfo
argument_list|,
name|NodeIDsInfo
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|LabelsToNodesInfo ()
specifier|public
name|LabelsToNodesInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|LabelsToNodesInfo (Map<NodeLabelInfo, NodeIDsInfo> labelsToNodes)
specifier|public
name|LabelsToNodesInfo
parameter_list|(
name|Map
argument_list|<
name|NodeLabelInfo
argument_list|,
name|NodeIDsInfo
argument_list|>
name|labelsToNodes
parameter_list|)
block|{
name|this
operator|.
name|labelsToNodes
operator|=
name|labelsToNodes
expr_stmt|;
block|}
DECL|method|getLabelsToNodes ()
specifier|public
name|Map
argument_list|<
name|NodeLabelInfo
argument_list|,
name|NodeIDsInfo
argument_list|>
name|getLabelsToNodes
parameter_list|()
block|{
return|return
name|labelsToNodes
return|;
block|}
block|}
end_class

end_unit

