begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Collection
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
name|timeline
operator|.
name|TimelineEntities
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
name|timeline
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
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineEvents
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
name|timeline
operator|.
name|TimelineDomain
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
name|timeline
operator|.
name|TimelineDomains
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
name|timeline
operator|.
name|TimelineDataManager
operator|.
name|CheckAcl
import|;
end_import

begin_comment
comment|/**  * This interface is for retrieving timeline information.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|TimelineReader
specifier|public
interface|interface
name|TimelineReader
block|{
comment|/**    * Possible fields to retrieve for {@link #getEntities} and {@link #getEntity}    * .    */
DECL|enum|Field
enum|enum
name|Field
block|{
DECL|enumConstant|EVENTS
name|EVENTS
block|,
DECL|enumConstant|RELATED_ENTITIES
name|RELATED_ENTITIES
block|,
DECL|enumConstant|PRIMARY_FILTERS
name|PRIMARY_FILTERS
block|,
DECL|enumConstant|OTHER_INFO
name|OTHER_INFO
block|,
DECL|enumConstant|LAST_EVENT_ONLY
name|LAST_EVENT_ONLY
block|}
comment|/**    * Default limit for {@link #getEntities} and {@link #getEntityTimelines}.    */
DECL|field|DEFAULT_LIMIT
specifier|final
name|long
name|DEFAULT_LIMIT
init|=
literal|100
decl_stmt|;
comment|/**    * This method retrieves a list of entity information, {@link TimelineEntity},    * sorted by the starting timestamp for the entity, descending. The starting    * timestamp of an entity is a timestamp specified by the client. If it is not    * explicitly specified, it will be chosen by the store to be the earliest    * timestamp of the events received in the first put for the entity.    *     * @param entityType    *          The type of entities to return (required).    * @param limit    *          A limit on the number of entities to return. If null, defaults to    *          {@link #DEFAULT_LIMIT}.    * @param windowStart    *          The earliest start timestamp to retrieve (exclusive). If null,    *          defaults to retrieving all entities until the limit is reached.    * @param windowEnd    *          The latest start timestamp to retrieve (inclusive). If null,    *          defaults to {@link Long#MAX_VALUE}    * @param fromId    *          If fromId is not null, retrieve entities earlier than and    *          including the specified ID. If no start time is found for the    *          specified ID, an empty list of entities will be returned. The    *          windowEnd parameter will take precedence if the start time of this    *          entity falls later than windowEnd.    * @param fromTs    *          If fromTs is not null, ignore entities that were inserted into the    *          store after the given timestamp. The entity's insert timestamp    *          used for this comparison is the store's system time when the first    *          put for the entity was received (not the entity's start time).    * @param primaryFilter    *          Retrieves only entities that have the specified primary filter. If    *          null, retrieves all entities. This is an indexed retrieval, and no    *          entities that do not match the filter are scanned.    * @param secondaryFilters    *          Retrieves only entities that have exact matches for all the    *          specified filters in their primary filters or other info. This is    *          not an indexed retrieval, so all entities are scanned but only    *          those matching the filters are returned.    * @param fieldsToRetrieve    *          Specifies which fields of the entity object to retrieve (see    *          {@link Field}). If the set of fields contains    *          {@link Field#LAST_EVENT_ONLY} and not {@link Field#EVENTS}, the    *          most recent event for each entity is retrieved. If null, retrieves    *          all fields.    * @return An {@link TimelineEntities} object.    * @throws IOException    */
DECL|method|getEntities (String entityType, Long limit, Long windowStart, Long windowEnd, String fromId, Long fromTs, NameValuePair primaryFilter, Collection<NameValuePair> secondaryFilters, EnumSet<Field> fieldsToRetrieve, CheckAcl checkAcl)
name|TimelineEntities
name|getEntities
parameter_list|(
name|String
name|entityType
parameter_list|,
name|Long
name|limit
parameter_list|,
name|Long
name|windowStart
parameter_list|,
name|Long
name|windowEnd
parameter_list|,
name|String
name|fromId
parameter_list|,
name|Long
name|fromTs
parameter_list|,
name|NameValuePair
name|primaryFilter
parameter_list|,
name|Collection
argument_list|<
name|NameValuePair
argument_list|>
name|secondaryFilters
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
parameter_list|,
name|CheckAcl
name|checkAcl
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method retrieves the entity information for a given entity.    *     * @param entityId    *          The entity whose information will be retrieved.    * @param entityType    *          The type of the entity.    * @param fieldsToRetrieve    *          Specifies which fields of the entity object to retrieve (see    *          {@link Field}). If the set of fields contains    *          {@link Field#LAST_EVENT_ONLY} and not {@link Field#EVENTS}, the    *          most recent event for each entity is retrieved. If null, retrieves    *          all fields.    * @return An {@link TimelineEntity} object.    * @throws IOException    */
DECL|method|getEntity (String entityId, String entityType, EnumSet<Field> fieldsToRetrieve)
name|TimelineEntity
name|getEntity
parameter_list|(
name|String
name|entityId
parameter_list|,
name|String
name|entityType
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
comment|/**    * This method retrieves the events for a list of entities all of the same    * entity type. The events for each entity are sorted in order of their    * timestamps, descending.    *     * @param entityType    *          The type of entities to retrieve events for.    * @param entityIds    *          The entity IDs to retrieve events for.    * @param limit    *          A limit on the number of events to return for each entity. If    *          null, defaults to {@link #DEFAULT_LIMIT} events per entity.    * @param windowStart    *          If not null, retrieves only events later than the given time    *          (exclusive)    * @param windowEnd    *          If not null, retrieves only events earlier than the given time    *          (inclusive)    * @param eventTypes    *          Restricts the events returned to the given types. If null, events    *          of all types will be returned.    * @return An {@link TimelineEvents} object.    * @throws IOException    */
DECL|method|getEntityTimelines (String entityType, SortedSet<String> entityIds, Long limit, Long windowStart, Long windowEnd, Set<String> eventTypes)
name|TimelineEvents
name|getEntityTimelines
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
name|Long
name|limit
parameter_list|,
name|Long
name|windowStart
parameter_list|,
name|Long
name|windowEnd
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|eventTypes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method retrieves the domain information for a given ID.    *     * @return a {@link TimelineDomain} object.    * @throws IOException    */
DECL|method|getDomain ( String domainId)
name|TimelineDomain
name|getDomain
parameter_list|(
name|String
name|domainId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * This method retrieves all the domains that belong to a given owner.    * The domains are sorted according to the created time firstly and the    * modified time secondly in descending order.    *     * @param owner    *          the domain owner    * @return an {@link TimelineDomains} object.    * @throws IOException    */
DECL|method|getDomains (String owner)
name|TimelineDomains
name|getDomains
parameter_list|(
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

