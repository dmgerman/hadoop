begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage
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
name|timelineservice
operator|.
name|storage
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
name|EnumSet
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|Unstable
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
name|Service
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
name|timelineservice
operator|.
name|FlowActivityEntity
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
name|timelineservice
operator|.
name|FlowRunEntity
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
name|timelineservice
operator|.
name|TimelineEntity
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
name|timelineservice
operator|.
name|reader
operator|.
name|filter
operator|.
name|TimelineFilterList
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
name|timelineservice
operator|.
name|reader
operator|.
name|filter
operator|.
name|TimelinePrefixFilter
import|;
end_import

begin_comment
comment|/** ATSv2 reader interface. */
end_comment

begin_interface
annotation|@
name|Private
annotation|@
name|Unstable
DECL|interface|TimelineReader
specifier|public
interface|interface
name|TimelineReader
extends|extends
name|Service
block|{
comment|/**    * Default limit for {@link #getEntities}.    */
DECL|field|DEFAULT_LIMIT
name|long
name|DEFAULT_LIMIT
init|=
literal|100
decl_stmt|;
comment|/**    * Possible fields to retrieve for {@link #getEntities} and    * {@link #getEntity}.    */
DECL|enum|Field
specifier|public
enum|enum
name|Field
block|{
DECL|enumConstant|ALL
name|ALL
block|,
DECL|enumConstant|EVENTS
name|EVENTS
block|,
DECL|enumConstant|INFO
name|INFO
block|,
DECL|enumConstant|METRICS
name|METRICS
block|,
DECL|enumConstant|CONFIGS
name|CONFIGS
block|,
DECL|enumConstant|RELATES_TO
name|RELATES_TO
block|,
DECL|enumConstant|IS_RELATED_TO
name|IS_RELATED_TO
block|}
comment|/**    *<p>The API to fetch the single entity given the entity identifier in the    * scope of the given context.</p>    *    * @param userId    *    Context user Id(optional).    * @param clusterId    *    Context cluster Id(mandatory).    * @param flowName    *    Context flow Id (optional).    * @param flowRunId    *    Context flow run Id (optional).    * @param appId    *    Context app Id (mandatory)    * @param entityType    *    Entity type (mandatory)    * @param entityId    *    Entity Id (mandatory)    * @param confsToRetrieve    *    Used for deciding which configs to return in response. This is    *    represented as a {@link TimelineFilterList} object containing    *    {@link TimelinePrefixFilter} objects. These can either be exact config    *    keys' or prefixes which are then compared against config keys' to decide    *    configs to return in response.    * @param metricsToRetrieve    *    Used for deciding which metrics to return in response. This is    *    represented as a {@link TimelineFilterList} object containing    *    {@link TimelinePrefixFilter} objects. These can either be exact metric    *    ids' or prefixes which are then compared against metric ids' to decide    *    metrics to return in response.    * @param fieldsToRetrieve    *    Specifies which fields of the entity object to retrieve(optional), see    *    {@link Field}. If null, retrieves 4 fields namely entity id,    *    entity type and entity created time. All fields will be returned if    *    {@link Field#ALL} is specified.    * @return a {@link TimelineEntity} instance or null. The entity will    *    contain the metadata plus the given fields to retrieve.    *    If entityType is YARN_FLOW_RUN, entity returned is of type    *    {@link FlowRunEntity}.    *    For all other entity types, entity returned is of type    *    {@link TimelineEntity}.    * @throws IOException    */
DECL|method|getEntity (String userId, String clusterId, String flowName, Long flowRunId, String appId, String entityType, String entityId, TimelineFilterList confsToRetrieve, TimelineFilterList metricsToRetrieve, EnumSet<Field> fieldsToRetrieve)
name|TimelineEntity
name|getEntity
parameter_list|(
name|String
name|userId
parameter_list|,
name|String
name|clusterId
parameter_list|,
name|String
name|flowName
parameter_list|,
name|Long
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|String
name|entityId
parameter_list|,
name|TimelineFilterList
name|confsToRetrieve
parameter_list|,
name|TimelineFilterList
name|metricsToRetrieve
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    *<p>The API to search for a set of entities of the given the entity type in    * the scope of the given context which matches the given predicates. The    * predicates include the created time window, limit to number of entities to    * be returned, and the entities can be filtered by checking whether they    * contain the given info/configs entries in the form of key/value pairs,    * given metrics in the form of metricsIds and its relation with metric    * values, given events in the form of the Ids, and whether they relate to/are    * related to other entities. For those parameters which have multiple    * entries, the qualified entity needs to meet all or them.</p>    *    * @param userId    *    Context user Id(optional).    * @param clusterId    *    Context cluster Id(mandatory).    * @param flowName    *    Context flow Id (optional).    * @param flowRunId    *    Context flow run Id (optional).    * @param appId    *    Context app Id (mandatory)    * @param entityType    *    Entity type (mandatory)    * @param limit    *    A limit on the number of entities to return (optional). If null or<=0,    *    defaults to {@link #DEFAULT_LIMIT}.    * @param createdTimeBegin    *    Matched entities should not be created before this timestamp (optional).    *    If null or<=0, defaults to 0.    * @param createdTimeEnd    *    Matched entities should not be created after this timestamp (optional).    *    If null or<=0, defaults to {@link Long#MAX_VALUE}.    * @param relatesTo    *    Matched entities should relate to given entities (optional).    * @param isRelatedTo    *    Matched entities should be related to given entities (optional).    * @param infoFilters    *    Matched entities should have exact matches to the given info represented    *    as key-value pairs (optional). If null or empty, the filter is not    *    applied.    * @param configFilters    *    Matched entities should have exact matches to the given configs    *    represented as key-value pairs (optional). If null or empty, the filter    *    is not applied.    * @param metricFilters    *    Matched entities should contain the given metrics (optional). If null    *    or empty, the filter is not applied.    * @param eventFilters    *    Matched entities should contain the given events (optional). If null    *    or empty, the filter is not applied.    * @param confsToRetrieve    *    Used for deciding which configs to return in response. This is    *    represented as a {@link TimelineFilterList} object containing    *    {@link TimelinePrefixFilter} objects. These can either be exact config    *    keys' or prefixes which are then compared against config keys' to decide    *    configs(inside entities) to return in response. This should not be    *    confused with configFilters which is used to decide which entities to    *    return instead.    * @param metricsToRetrieve    *    Used for deciding which metrics to return in response. This is    *    represented as a {@link TimelineFilterList} object containing    *    {@link TimelinePrefixFilter} objects. These can either be exact metric    *    ids' or prefixes which are then compared against metric ids' to decide    *    metrics(inside entities) to return in response. This should not be    *    confused with metricFilters which is used to decide which entities to    *    return instead.    * @param fieldsToRetrieve    *    Specifies which fields of the entity object to retrieve(optional), see    *    {@link Field}. If null, retrieves 4 fields namely entity id,    *    entity type and entity created time. All fields will be returned if    *    {@link Field#ALL} is specified.    * @return A set of {@link TimelineEntity} instances of the given entity type    *    in the given context scope which matches the given predicates    *    ordered by created time, descending. Each entity will only contain the    *    metadata(id, type and created time) plus the given fields to retrieve.    *    If entityType is YARN_FLOW_ACTIVITY, entities returned are of type    *    {@link FlowActivityEntity}.    *    If entityType is YARN_FLOW_RUN, entities returned are of type    *    {@link FlowRunEntity}.    *    For all other entity types, entities returned are of type    *    {@link TimelineEntity}.    * @throws IOException    */
DECL|method|getEntities (String userId, String clusterId, String flowName, Long flowRunId, String appId, String entityType, Long limit, Long createdTimeBegin, Long createdTimeEnd, Map<String, Set<String>> relatesTo, Map<String, Set<String>> isRelatedTo, Map<String, Object> infoFilters, Map<String, String> configFilters, Set<String> metricFilters, Set<String> eventFilters, TimelineFilterList confsToRetrieve, TimelineFilterList metricsToRetrieve, EnumSet<Field> fieldsToRetrieve)
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|getEntities
parameter_list|(
name|String
name|userId
parameter_list|,
name|String
name|clusterId
parameter_list|,
name|String
name|flowName
parameter_list|,
name|Long
name|flowRunId
parameter_list|,
name|String
name|appId
parameter_list|,
name|String
name|entityType
parameter_list|,
name|Long
name|limit
parameter_list|,
name|Long
name|createdTimeBegin
parameter_list|,
name|Long
name|createdTimeEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatesTo
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|isRelatedTo
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|infoFilters
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configFilters
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|metricFilters
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|eventFilters
parameter_list|,
name|TimelineFilterList
name|confsToRetrieve
parameter_list|,
name|TimelineFilterList
name|metricsToRetrieve
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

