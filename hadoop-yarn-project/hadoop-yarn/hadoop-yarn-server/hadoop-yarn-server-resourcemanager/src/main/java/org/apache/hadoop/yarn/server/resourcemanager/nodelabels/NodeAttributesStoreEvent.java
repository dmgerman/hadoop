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
name|event
operator|.
name|AbstractEvent
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
name|nodelabels
operator|.
name|AttributeValue
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
name|AttributeMappingOperationType
import|;
end_import

begin_comment
comment|/**  * Event capturing details to store the Node Attributes in the backend store.  */
end_comment

begin_class
DECL|class|NodeAttributesStoreEvent
specifier|public
class|class
name|NodeAttributesStoreEvent
extends|extends
name|AbstractEvent
argument_list|<
name|NodeAttributesStoreEventType
argument_list|>
block|{
DECL|field|nodeAttributeMapping
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|NodeAttribute
argument_list|,
name|AttributeValue
argument_list|>
argument_list|>
name|nodeAttributeMapping
decl_stmt|;
DECL|field|operation
specifier|private
name|AttributeMappingOperationType
name|operation
decl_stmt|;
DECL|method|NodeAttributesStoreEvent ( Map<String, Map<NodeAttribute, AttributeValue>> nodeAttributeMappingList, AttributeMappingOperationType operation)
specifier|public
name|NodeAttributesStoreEvent
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|NodeAttribute
argument_list|,
name|AttributeValue
argument_list|>
argument_list|>
name|nodeAttributeMappingList
parameter_list|,
name|AttributeMappingOperationType
name|operation
parameter_list|)
block|{
name|super
argument_list|(
name|NodeAttributesStoreEventType
operator|.
name|STORE_ATTRIBUTES
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeAttributeMapping
operator|=
name|nodeAttributeMappingList
expr_stmt|;
name|this
operator|.
name|operation
operator|=
name|operation
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
DECL|method|getNodeAttributeMappingList ()
name|Map
argument_list|<
name|NodeAttribute
argument_list|,
name|AttributeValue
argument_list|>
argument_list|>
name|getNodeAttributeMappingList
parameter_list|()
block|{
return|return
name|nodeAttributeMapping
return|;
block|}
DECL|method|getOperation ()
specifier|public
name|AttributeMappingOperationType
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
block|}
end_class

end_unit

