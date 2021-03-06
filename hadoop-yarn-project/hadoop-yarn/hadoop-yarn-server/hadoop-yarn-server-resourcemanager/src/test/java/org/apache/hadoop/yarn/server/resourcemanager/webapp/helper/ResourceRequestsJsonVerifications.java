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
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONArray
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jettison
operator|.
name|json
operator|.
name|JSONObject
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
DECL|class|ResourceRequestsJsonVerifications
specifier|public
class|class
name|ResourceRequestsJsonVerifications
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
name|JSONObject
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
DECL|method|ResourceRequestsJsonVerifications (Builder builder)
name|ResourceRequestsJsonVerifications
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
DECL|method|verify (JSONObject requestInfo, ResourceRequest rr)
specifier|public
specifier|static
name|void
name|verify
parameter_list|(
name|JSONObject
name|requestInfo
parameter_list|,
name|ResourceRequest
name|rr
parameter_list|)
throws|throws
name|JSONException
block|{
name|createDefaultBuilder
argument_list|(
name|requestInfo
argument_list|,
name|rr
argument_list|)
operator|.
name|build
argument_list|()
operator|.
name|verify
argument_list|()
expr_stmt|;
block|}
DECL|method|verifyWithCustomResourceTypes (JSONObject requestInfo, ResourceRequest resourceRequest, List<String> expectedResourceTypes)
specifier|public
specifier|static
name|void
name|verifyWithCustomResourceTypes
parameter_list|(
name|JSONObject
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
throws|throws
name|JSONException
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
name|extractActualCustomResourceTypes
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
DECL|method|createDefaultBuilder (JSONObject requestInfo, ResourceRequest resourceRequest)
specifier|private
specifier|static
name|Builder
name|createDefaultBuilder
parameter_list|(
name|JSONObject
name|requestInfo
parameter_list|,
name|ResourceRequest
name|resourceRequest
parameter_list|)
block|{
return|return
operator|new
name|ResourceRequestsJsonVerifications
operator|.
name|Builder
argument_list|()
operator|.
name|withRequest
argument_list|(
name|resourceRequest
argument_list|)
operator|.
name|withRequestInfoJson
argument_list|(
name|requestInfo
argument_list|)
return|;
block|}
DECL|method|extractActualCustomResourceTypes ( JSONObject requestInfo, List<String> expectedResourceTypes)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|extractActualCustomResourceTypes
parameter_list|(
name|JSONObject
name|requestInfo
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedResourceTypes
parameter_list|)
throws|throws
name|JSONException
block|{
name|JSONObject
name|capability
init|=
name|requestInfo
operator|.
name|getJSONObject
argument_list|(
literal|"capability"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|resourceAndValue
init|=
name|extractCustomResorceTypeValues
argument_list|(
name|capability
argument_list|,
name|expectedResourceTypes
argument_list|)
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|resourceEntry
init|=
name|resourceAndValue
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Found resource type: "
operator|+
name|resourceEntry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is not in expected resource types: "
operator|+
name|expectedResourceTypes
argument_list|,
name|expectedResourceTypes
operator|.
name|contains
argument_list|(
name|resourceEntry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|resourceAndValue
return|;
block|}
DECL|method|extractCustomResorceTypeValues ( JSONObject capability, List<String> expectedResourceTypes)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|extractCustomResorceTypeValues
parameter_list|(
name|JSONObject
name|capability
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedResourceTypes
parameter_list|)
throws|throws
name|JSONException
block|{
name|assertTrue
argument_list|(
literal|"resourceCategory does not have resourceInformations: "
operator|+
name|capability
argument_list|,
name|capability
operator|.
name|has
argument_list|(
literal|"resourceInformations"
argument_list|)
argument_list|)
expr_stmt|;
name|JSONObject
name|resourceInformations
init|=
name|capability
operator|.
name|getJSONObject
argument_list|(
literal|"resourceInformations"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"resourceInformations does not have resourceInformation object: "
operator|+
name|resourceInformations
argument_list|,
name|resourceInformations
operator|.
name|has
argument_list|(
literal|"resourceInformation"
argument_list|)
argument_list|)
expr_stmt|;
name|JSONArray
name|customResources
init|=
name|resourceInformations
operator|.
name|getJSONArray
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
name|length
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
name|resourceValues
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
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|JSONObject
name|customResource
init|=
name|customResources
operator|.
name|getJSONObject
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Resource type does not have name field: "
operator|+
name|customResource
argument_list|,
name|customResource
operator|.
name|has
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Resource type does not have name resourceType field: "
operator|+
name|customResource
argument_list|,
name|customResource
operator|.
name|has
argument_list|(
literal|"resourceType"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Resource type does not have name units field: "
operator|+
name|customResource
argument_list|,
name|customResource
operator|.
name|has
argument_list|(
literal|"units"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Resource type does not have name value field: "
operator|+
name|customResource
argument_list|,
name|customResource
operator|.
name|has
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|customResource
operator|.
name|getString
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|unit
init|=
name|customResource
operator|.
name|getString
argument_list|(
literal|"units"
argument_list|)
decl_stmt|;
name|String
name|resourceType
init|=
name|customResource
operator|.
name|getString
argument_list|(
literal|"resourceType"
argument_list|)
decl_stmt|;
name|Long
name|value
init|=
name|customResource
operator|.
name|getLong
argument_list|(
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
literal|"Custom resource value "
operator|+
name|value
operator|+
literal|" is null!"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|resourceValues
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
name|resourceValues
return|;
block|}
DECL|method|verify ()
specifier|private
name|void
name|verify
parameter_list|()
throws|throws
name|JSONException
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
name|requestInfo
operator|.
name|getString
argument_list|(
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
name|requestInfo
operator|.
name|getInt
argument_list|(
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
name|requestInfo
operator|.
name|getBoolean
argument_list|(
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
name|requestInfo
operator|.
name|getInt
argument_list|(
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
name|requestInfo
operator|.
name|getString
argument_list|(
literal|"resourceName"
argument_list|)
argument_list|)
expr_stmt|;
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
name|requestInfo
operator|.
name|getJSONObject
argument_list|(
literal|"capability"
argument_list|)
operator|.
name|getLong
argument_list|(
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
name|requestInfo
operator|.
name|getJSONObject
argument_list|(
literal|"capability"
argument_list|)
operator|.
name|getLong
argument_list|(
literal|"vCores"
argument_list|)
argument_list|)
expr_stmt|;
name|verifyAtLeastOneCustomResourceIsSerialized
argument_list|()
expr_stmt|;
name|JSONObject
name|executionTypeRequest
init|=
name|requestInfo
operator|.
name|getJSONObject
argument_list|(
literal|"executionTypeRequest"
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
name|executionTypeRequest
operator|.
name|getString
argument_list|(
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
name|executionTypeRequest
operator|.
name|getBoolean
argument_list|(
literal|"enforceExecutionType"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * JSON serialization produces "invalid JSON" by default as maps are    * serialized like this:    * "customResources":{"entry":{"key":"customResource-1","value":"0"}}    * If the map has multiple keys then multiple entries will be serialized.    * Our json parser in tests cannot handle duplicates therefore only one    * custom resource will be in the parsed json. See:    * https://issues.apache.org/jira/browse/YARN-7505    */
DECL|method|verifyAtLeastOneCustomResourceIsSerialized ()
specifier|private
name|void
name|verifyAtLeastOneCustomResourceIsSerialized
parameter_list|()
block|{
name|boolean
name|resourceFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|expectedCustomResourceType
range|:
name|expectedCustomResourceTypes
control|)
block|{
if|if
condition|(
name|customResourceTypes
operator|.
name|containsKey
argument_list|(
name|expectedCustomResourceType
argument_list|)
condition|)
block|{
name|resourceFound
operator|=
literal|true
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
block|}
name|assertTrue
argument_list|(
literal|"No custom resource type can be found in the response!"
argument_list|,
name|resourceFound
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builder class for {@link ResourceRequestsJsonVerifications}.    */
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
name|JSONObject
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
DECL|method|withCustomResourceTypes ( Map<String, Long> customResourceTypes)
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
DECL|method|withRequestInfoJson (JSONObject requestInfo)
name|Builder
name|withRequestInfoJson
parameter_list|(
name|JSONObject
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
name|ResourceRequestsJsonVerifications
name|build
parameter_list|()
block|{
return|return
operator|new
name|ResourceRequestsJsonVerifications
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

