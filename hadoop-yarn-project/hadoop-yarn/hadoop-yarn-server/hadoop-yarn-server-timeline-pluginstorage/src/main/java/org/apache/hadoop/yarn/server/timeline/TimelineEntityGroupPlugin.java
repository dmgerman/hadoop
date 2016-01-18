begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
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
name|timeline
package|;
end_package

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
name|timeline
operator|.
name|TimelineEntityGroupId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|SortedSet
import|;
end_import

begin_comment
comment|/**  * Plugin to map a requested query ( or an Entity/set of Entities ) to a CacheID.  * The Cache ID is an identifier to the data set that needs to be queried to  * serve the response for the query.  */
end_comment

begin_class
DECL|class|TimelineEntityGroupPlugin
specifier|public
specifier|abstract
class|class
name|TimelineEntityGroupPlugin
block|{
comment|/**    * Get the {@link TimelineEntityGroupId}s for the data sets that need to be    * scanned to serve the query.    *    * @param entityType Entity Type being queried    * @param primaryFilter Primary filter being applied    * @param secondaryFilters Secondary filters being applied in the query    * @return {@link org.apache.hadoop.yarn.api.records.timeline.TimelineEntityGroupId}    */
DECL|method|getTimelineEntityGroupId ( String entityType, NameValuePair primaryFilter, Collection<NameValuePair> secondaryFilters)
specifier|public
specifier|abstract
name|Set
argument_list|<
name|TimelineEntityGroupId
argument_list|>
name|getTimelineEntityGroupId
parameter_list|(
name|String
name|entityType
parameter_list|,
name|NameValuePair
name|primaryFilter
parameter_list|,
name|Collection
argument_list|<
name|NameValuePair
argument_list|>
name|secondaryFilters
parameter_list|)
function_decl|;
comment|/**    * Get the {@link TimelineEntityGroupId}s for the data sets that need to be    * scanned to serve the query.    *    * @param entityType Entity Type being queried    * @param entityId Entity Id being requested    * @return {@link org.apache.hadoop.yarn.api.records.timeline.TimelineEntityGroupId}    */
DECL|method|getTimelineEntityGroupId ( String entityId, String entityType)
specifier|public
specifier|abstract
name|Set
argument_list|<
name|TimelineEntityGroupId
argument_list|>
name|getTimelineEntityGroupId
parameter_list|(
name|String
name|entityId
parameter_list|,
name|String
name|entityType
parameter_list|)
function_decl|;
comment|/**    * Get the {@link TimelineEntityGroupId}s for the data sets that need to be    * scanned to serve the query.    *    * @param entityType Entity Type being queried    * @param entityIds Entity Ids being requested    * @param eventTypes Event Types being requested    * @return {@link org.apache.hadoop.yarn.api.records.timeline.TimelineEntityGroupId}    */
DECL|method|getTimelineEntityGroupId ( String entityType, SortedSet<String> entityIds, Set<String> eventTypes)
specifier|public
specifier|abstract
name|Set
argument_list|<
name|TimelineEntityGroupId
argument_list|>
name|getTimelineEntityGroupId
parameter_list|(
name|String
name|entityType
parameter_list|,
name|SortedSet
argument_list|<
name|String
argument_list|>
name|entityIds
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|eventTypes
parameter_list|)
function_decl|;
block|}
end_class

end_unit

