begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.fairscheduler
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
name|fairscheduler
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
name|Set
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * This test helper class is primarily used by  * {@link TestRMWebServicesFairSchedulerCustomResourceTypes}.  */
end_comment

begin_class
DECL|class|FairSchedulerJsonVerifications
specifier|public
class|class
name|FairSchedulerJsonVerifications
block|{
DECL|field|RESOURCE_FIELDS
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|RESOURCE_FIELDS
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"minResources"
argument_list|,
literal|"amUsedResources"
argument_list|,
literal|"amMaxResources"
argument_list|,
literal|"fairResources"
argument_list|,
literal|"clusterResources"
argument_list|,
literal|"reservedResources"
argument_list|,
literal|"maxResources"
argument_list|,
literal|"usedResources"
argument_list|,
literal|"steadyFairResources"
argument_list|,
literal|"demandResources"
argument_list|)
decl_stmt|;
DECL|field|customResourceTypes
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|customResourceTypes
decl_stmt|;
DECL|method|FairSchedulerJsonVerifications (List<String> customResourceTypes)
name|FairSchedulerJsonVerifications
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|customResourceTypes
parameter_list|)
block|{
name|this
operator|.
name|customResourceTypes
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|customResourceTypes
argument_list|)
expr_stmt|;
block|}
DECL|method|verify (JSONObject jsonObject)
specifier|public
name|void
name|verify
parameter_list|(
name|JSONObject
name|jsonObject
parameter_list|)
block|{
try|try
block|{
name|verifyResourcesContainDefaultResourceTypes
argument_list|(
name|jsonObject
argument_list|,
name|RESOURCE_FIELDS
argument_list|)
expr_stmt|;
name|verifyResourcesContainCustomResourceTypes
argument_list|(
name|jsonObject
argument_list|,
name|RESOURCE_FIELDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JSONException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|verifyResourcesContainDefaultResourceTypes (JSONObject queue, Set<String> resourceCategories)
specifier|private
name|void
name|verifyResourcesContainDefaultResourceTypes
parameter_list|(
name|JSONObject
name|queue
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|resourceCategories
parameter_list|)
throws|throws
name|JSONException
block|{
for|for
control|(
name|String
name|resourceCategory
range|:
name|resourceCategories
control|)
block|{
name|boolean
name|hasResourceCategory
init|=
name|queue
operator|.
name|has
argument_list|(
name|resourceCategory
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Queue "
operator|+
name|queue
operator|+
literal|" does not have resource category key: "
operator|+
name|resourceCategory
argument_list|,
name|hasResourceCategory
argument_list|)
expr_stmt|;
name|verifyResourceContainsDefaultResourceTypes
argument_list|(
name|queue
operator|.
name|getJSONObject
argument_list|(
name|resourceCategory
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyResourceContainsDefaultResourceTypes ( JSONObject jsonObject)
specifier|private
name|void
name|verifyResourceContainsDefaultResourceTypes
parameter_list|(
name|JSONObject
name|jsonObject
parameter_list|)
block|{
name|Object
name|memory
init|=
name|jsonObject
operator|.
name|opt
argument_list|(
literal|"memory"
argument_list|)
decl_stmt|;
name|Object
name|vCores
init|=
name|jsonObject
operator|.
name|opt
argument_list|(
literal|"vCores"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Key 'memory' not found in: "
operator|+
name|jsonObject
argument_list|,
name|memory
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Key 'vCores' not found in: "
operator|+
name|jsonObject
argument_list|,
name|vCores
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyResourcesContainCustomResourceTypes (JSONObject queue, Set<String> resourceCategories)
specifier|private
name|void
name|verifyResourcesContainCustomResourceTypes
parameter_list|(
name|JSONObject
name|queue
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|resourceCategories
parameter_list|)
throws|throws
name|JSONException
block|{
for|for
control|(
name|String
name|resourceCategory
range|:
name|resourceCategories
control|)
block|{
name|assertTrue
argument_list|(
literal|"Queue "
operator|+
name|queue
operator|+
literal|" does not have resource category key: "
operator|+
name|resourceCategory
argument_list|,
name|queue
operator|.
name|has
argument_list|(
name|resourceCategory
argument_list|)
argument_list|)
expr_stmt|;
name|verifyResourceContainsAllCustomResourceTypes
argument_list|(
name|queue
operator|.
name|getJSONObject
argument_list|(
name|resourceCategory
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verifyResourceContainsAllCustomResourceTypes ( JSONObject resourceCategory)
specifier|private
name|void
name|verifyResourceContainsAllCustomResourceTypes
parameter_list|(
name|JSONObject
name|resourceCategory
parameter_list|)
throws|throws
name|JSONException
block|{
name|assertTrue
argument_list|(
literal|"resourceCategory does not have resourceInformations: "
operator|+
name|resourceCategory
argument_list|,
name|resourceCategory
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
name|resourceCategory
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
name|customResourceTypes
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
name|customResourceTypes
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
block|}
block|}
block|}
end_class

end_unit

