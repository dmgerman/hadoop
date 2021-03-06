begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.helper
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
name|helper
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|ResourceTypes
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
name|ResourceInformation
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
name|ResourceRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertTrue
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|helper
operator|.
name|XmlCustomResourceTypeTestCase
operator|.
name|toXml
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
name|yarn
operator|.
name|webapp
operator|.
name|WebServicesTestUtils
operator|.
name|getXmlBoolean
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
name|yarn
operator|.
name|webapp
operator|.
name|WebServicesTestUtils
operator|.
name|getXmlInt
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
name|yarn
operator|.
name|webapp
operator|.
name|WebServicesTestUtils
operator|.
name|getXmlLong
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
name|yarn
operator|.
name|webapp
operator|.
name|WebServicesTestUtils
operator|.
name|getXmlString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * Performs value verifications on  * {@link org.apache.hadoop.yarn.server.resourcemanager.webapp.dao.ResourceRequestInfo}  * objects against the values of {@link ResourceRequest}. With the help of the  * {@link Builder}, users can also make verifications of the custom resource  * types and its values.  */
end_comment

begin_class
DECL|class|ResourceRequestsXmlVerifications
specifier|public
class|class
name|ResourceRequestsXmlVerifications
block|{
DECL|field|resourceRequest
specifier|private
specifier|final
name|ResourceRequest
name|resourceRequest
decl_stmt|;
DECL|field|requestInfo
specifier|private
specifier|final
name|Element
name|requestInfo
decl_stmt|;
DECL|field|customResourceTypes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|customResourceTypes
decl_stmt|;
DECL|field|expectedCustomResourceTypes
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expectedCustomResourceTypes
decl_stmt|;
DECL|method|ResourceRequestsXmlVerifications (Builder builder)
name|ResourceRequestsXmlVerifications
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|resourceRequest
operator|=
name|builder
operator|.
name|resourceRequest
expr_stmt|;
name|this
operator|.
name|requestInfo
operator|=
name|builder
operator|.
name|requestInfo
expr_stmt|;
name|this
operator|.
name|customResourceTypes
operator|=
name|builder
operator|.
name|customResourceTypes
expr_stmt|;
name|this
operator|.
name|expectedCustomResourceTypes
operator|=
name|builder
operator|.
name|expectedCustomResourceTypes
expr_stmt|;
block|}
DECL|method|verifyWithCustomResourceTypes (Element requestInfo, ResourceRequest resourceRequest, List<String> expectedResourceTypes)
specifier|public
specifier|static
name|void
name|verifyWithCustomResourceTypes
parameter_list|(
name|Element
name|requestInfo
parameter_list|,
name|ResourceRequest
name|resourceRequest
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedResourceTypes
parameter_list|)
block|{
name|createDefaultBuilder
argument_list|(
name|requestInfo
argument_list|,
name|resourceRequest
argument_list|)
operator|.
name|withExpectedCustomResourceTypes
argument_list|(
name|expectedResourceTypes
argument_list|)
operator|.
name|withCustomResourceTypes
argument_list|(
name|extractActualCustomResourceType
argument_list|(
name|requestInfo
argument_list|,
name|expectedResourceTypes
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
DECL|method|createDefaultBuilder (Element requestInfo, ResourceRequest resourceRequest)
specifier|private
specifier|static
name|Builder
name|createDefaultBuilder
parameter_list|(
name|Element
name|requestInfo
parameter_list|,
name|ResourceRequest
name|resourceRequest
parameter_list|)
block|{
return|return
operator|new
name|ResourceRequestsXmlVerifications
operator|.
name|Builder
argument_list|()
operator|.
name|withRequest
argument_list|(
name|resourceRequest
argument_list|)
operator|.
name|withRequestInfo
argument_list|(
name|requestInfo
argument_list|)
return|;
block|}
DECL|method|extractActualCustomResourceType ( Element requestInfo, List<String> expectedResourceTypes)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|extractActualCustomResourceType
parameter_list|(
name|Element
name|requestInfo
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedResourceTypes
parameter_list|)
block|{
name|Element
name|capability
init|=
operator|(
name|Element
operator|)
name|requestInfo
operator|.
name|getElementsByTagName
argument_list|(
literal|"capability"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|extractCustomResorceTypes
argument_list|(
name|capability
argument_list|,
name|Sets
operator|.
name|newHashSet
argument_list|(
name|expectedResourceTypes
argument_list|)
argument_list|)
return|;
block|}
DECL|method|extractCustomResorceTypes (Element capability, Set<String> expectedResourceTypes)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|extractCustomResorceTypes
parameter_list|(
name|Element
name|capability
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|expectedResourceTypes
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|toXml
argument_list|(
name|capability
argument_list|)
operator|+
literal|" should have only one resourceInformations child!"
argument_list|,
literal|1
argument_list|,
name|capability
operator|.
name|getElementsByTagName
argument_list|(
literal|"resourceInformations"
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|Element
name|resourceInformations
init|=
operator|(
name|Element
operator|)
name|capability
operator|.
name|getElementsByTagName
argument_list|(
literal|"resourceInformations"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NodeList
name|customResources
init|=
name|resourceInformations
operator|.
name|getElementsByTagName
argument_list|(
literal|"resourceInformation"
argument_list|)
decl_stmt|;
comment|// customResources will include vcores / memory as well
name|assertEquals
argument_list|(
literal|"Different number of custom resource types found than expected"
argument_list|,
name|expectedResourceTypes
operator|.
name|size
argument_list|()
argument_list|,
name|customResources
operator|.
name|getLength
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|resourceTypesAndValues
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|customResources
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|customResource
init|=
operator|(
name|Element
operator|)
name|customResources
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|getXmlString
argument_list|(
name|customResource
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|unit
init|=
name|getXmlString
argument_list|(
name|customResource
argument_list|,
literal|"units"
argument_list|)
decl_stmt|;
name|String
name|resourceType
init|=
name|getXmlString
argument_list|(
name|customResource
argument_list|,
literal|"resourceType"
argument_list|)
decl_stmt|;
name|Long
name|value
init|=
name|getXmlLong
argument_list|(
name|customResource
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ResourceInformation
operator|.
name|MEMORY_URI
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|||
name|ResourceInformation
operator|.
name|VCORES_URI
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|assertTrue
argument_list|(
literal|"Custom resource type "
operator|+
name|name
operator|+
literal|" not found"
argument_list|,
name|expectedResourceTypes
operator|.
name|contains
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"k"
argument_list|,
name|unit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ResourceTypes
operator|.
name|COUNTABLE
argument_list|,
name|ResourceTypes
operator|.
name|valueOf
argument_list|(
name|resourceType
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Resource value should not be null for resource type "
operator|+
name|resourceType
operator|+
literal|", listing xml contents: "
operator|+
name|toXml
argument_list|(
name|customResource
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|resourceTypesAndValues
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|resourceTypesAndValues
return|;
block|}
DECL|method|verify ()
specifier|private
name|void
name|verify
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"nodeLabelExpression doesn't match"
argument_list|,
name|resourceRequest
operator|.
name|getNodeLabelExpression
argument_list|()
argument_list|,
name|getXmlString
argument_list|(
name|requestInfo
argument_list|,
literal|"nodeLabelExpression"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"numContainers doesn't match"
argument_list|,
name|resourceRequest
operator|.
name|getNumContainers
argument_list|()
argument_list|,
name|getXmlInt
argument_list|(
name|requestInfo
argument_list|,
literal|"numContainers"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"relaxLocality doesn't match"
argument_list|,
name|resourceRequest
operator|.
name|getRelaxLocality
argument_list|()
argument_list|,
name|getXmlBoolean
argument_list|(
name|requestInfo
argument_list|,
literal|"relaxLocality"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"priority does not match"
argument_list|,
name|resourceRequest
operator|.
name|getPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|,
name|getXmlInt
argument_list|(
name|requestInfo
argument_list|,
literal|"priority"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"resourceName does not match"
argument_list|,
name|resourceRequest
operator|.
name|getResourceName
argument_list|()
argument_list|,
name|getXmlString
argument_list|(
name|requestInfo
argument_list|,
literal|"resourceName"
argument_list|)
argument_list|)
expr_stmt|;
name|Element
name|capability
init|=
operator|(
name|Element
operator|)
name|requestInfo
operator|.
name|getElementsByTagName
argument_list|(
literal|"capability"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"memory does not match"
argument_list|,
name|resourceRequest
operator|.
name|getCapability
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|getXmlLong
argument_list|(
name|capability
argument_list|,
literal|"memory"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"vCores does not match"
argument_list|,
name|resourceRequest
operator|.
name|getCapability
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|getXmlLong
argument_list|(
name|capability
argument_list|,
literal|"vCores"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|expectedCustomResourceType
range|:
name|expectedCustomResourceTypes
control|)
block|{
name|assertTrue
argument_list|(
literal|"Custom resource type "
operator|+
name|expectedCustomResourceType
operator|+
literal|" cannot be found!"
argument_list|,
name|customResourceTypes
operator|.
name|containsKey
argument_list|(
name|expectedCustomResourceType
argument_list|)
argument_list|)
expr_stmt|;
name|Long
name|resourceValue
init|=
name|customResourceTypes
operator|.
name|get
argument_list|(
name|expectedCustomResourceType
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Resource value should not be null!"
argument_list|,
name|resourceValue
argument_list|)
expr_stmt|;
block|}
name|Element
name|executionTypeRequest
init|=
operator|(
name|Element
operator|)
name|requestInfo
operator|.
name|getElementsByTagName
argument_list|(
literal|"executionTypeRequest"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"executionType does not match"
argument_list|,
name|resourceRequest
operator|.
name|getExecutionTypeRequest
argument_list|()
operator|.
name|getExecutionType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|getXmlString
argument_list|(
name|executionTypeRequest
argument_list|,
literal|"executionType"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"enforceExecutionType does not match"
argument_list|,
name|resourceRequest
operator|.
name|getExecutionTypeRequest
argument_list|()
operator|.
name|getEnforceExecutionType
argument_list|()
argument_list|,
name|getXmlBoolean
argument_list|(
name|executionTypeRequest
argument_list|,
literal|"enforceExecutionType"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builder class for {@link ResourceRequestsXmlVerifications}.    */
DECL|class|Builder
specifier|public
specifier|static
specifier|final
class|class
name|Builder
block|{
DECL|field|expectedCustomResourceTypes
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|expectedCustomResourceTypes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|customResourceTypes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|customResourceTypes
decl_stmt|;
DECL|field|resourceRequest
specifier|private
name|ResourceRequest
name|resourceRequest
decl_stmt|;
DECL|field|requestInfo
specifier|private
name|Element
name|requestInfo
decl_stmt|;
DECL|method|Builder ()
name|Builder
parameter_list|()
block|{     }
DECL|method|create ()
specifier|public
specifier|static
name|Builder
name|create
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|withExpectedCustomResourceTypes ( List<String> expectedCustomResourceTypes)
name|Builder
name|withExpectedCustomResourceTypes
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|expectedCustomResourceTypes
parameter_list|)
block|{
name|this
operator|.
name|expectedCustomResourceTypes
operator|=
name|expectedCustomResourceTypes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withCustomResourceTypes (Map<String, Long> customResourceTypes)
name|Builder
name|withCustomResourceTypes
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|customResourceTypes
parameter_list|)
block|{
name|this
operator|.
name|customResourceTypes
operator|=
name|customResourceTypes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withRequest (ResourceRequest resourceRequest)
name|Builder
name|withRequest
parameter_list|(
name|ResourceRequest
name|resourceRequest
parameter_list|)
block|{
name|this
operator|.
name|resourceRequest
operator|=
name|resourceRequest
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withRequestInfo (Element requestInfo)
name|Builder
name|withRequestInfo
parameter_list|(
name|Element
name|requestInfo
parameter_list|)
block|{
name|this
operator|.
name|requestInfo
operator|=
name|requestInfo
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|ResourceRequestsXmlVerifications
name|build
parameter_list|()
block|{
return|return
operator|new
name|ResourceRequestsXmlVerifications
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

