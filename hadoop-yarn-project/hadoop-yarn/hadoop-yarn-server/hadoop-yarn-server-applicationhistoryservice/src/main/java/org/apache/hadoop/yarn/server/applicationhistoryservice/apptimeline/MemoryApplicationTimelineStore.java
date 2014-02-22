begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.apptimeline
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
name|applicationhistoryservice
operator|.
name|apptimeline
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Collections
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
name|HashMap
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
name|PriorityQueue
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
name|AbstractService
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
name|apptimeline
operator|.
name|ATSEntities
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
name|apptimeline
operator|.
name|ATSEntity
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
name|apptimeline
operator|.
name|ATSEvent
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
name|apptimeline
operator|.
name|ATSEvents
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
name|apptimeline
operator|.
name|ATSEvents
operator|.
name|ATSEventsOfOneEntity
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
name|apptimeline
operator|.
name|ATSPutErrors
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
name|apptimeline
operator|.
name|ATSPutErrors
operator|.
name|ATSPutError
import|;
end_import

begin_comment
comment|/**  * In-memory implementation of {@link ApplicationTimelineStore}. This  * implementation is for test purpose only. If users improperly instantiate it,  * they may encounter reading and writing history data in different memory  * store.  *   */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|MemoryApplicationTimelineStore
specifier|public
class|class
name|MemoryApplicationTimelineStore
extends|extends
name|AbstractService
implements|implements
name|ApplicationTimelineStore
block|{
DECL|field|entities
specifier|private
name|Map
argument_list|<
name|EntityIdentifier
argument_list|,
name|ATSEntity
argument_list|>
name|entities
init|=
operator|new
name|HashMap
argument_list|<
name|EntityIdentifier
argument_list|,
name|ATSEntity
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MemoryApplicationTimelineStore ()
specifier|public
name|MemoryApplicationTimelineStore
parameter_list|()
block|{
name|super
argument_list|(
name|MemoryApplicationTimelineStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEntities (String entityType, Long limit, Long windowStart, Long windowEnd, NameValuePair primaryFilter, Collection<NameValuePair> secondaryFilters, EnumSet<Field> fields)
specifier|public
name|ATSEntities
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
name|fields
parameter_list|)
block|{
if|if
condition|(
name|limit
operator|==
literal|null
condition|)
block|{
name|limit
operator|=
name|DEFAULT_LIMIT
expr_stmt|;
block|}
if|if
condition|(
name|windowStart
operator|==
literal|null
condition|)
block|{
name|windowStart
operator|=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
if|if
condition|(
name|windowEnd
operator|==
literal|null
condition|)
block|{
name|windowEnd
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|Field
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ATSEntity
argument_list|>
name|entitiesSelected
init|=
operator|new
name|ArrayList
argument_list|<
name|ATSEntity
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ATSEntity
name|entity
range|:
operator|new
name|PriorityQueue
argument_list|<
name|ATSEntity
argument_list|>
argument_list|(
name|entities
operator|.
name|values
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|entitiesSelected
operator|.
name|size
argument_list|()
operator|>=
name|limit
condition|)
block|{
break|break;
block|}
if|if
condition|(
operator|!
name|entity
operator|.
name|getEntityType
argument_list|()
operator|.
name|equals
argument_list|(
name|entityType
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|entity
operator|.
name|getStartTime
argument_list|()
operator|<=
name|windowStart
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|entity
operator|.
name|getStartTime
argument_list|()
operator|>
name|windowEnd
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|primaryFilter
operator|!=
literal|null
operator|&&
operator|!
name|matchPrimaryFilter
argument_list|(
name|entity
operator|.
name|getPrimaryFilters
argument_list|()
argument_list|,
name|primaryFilter
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|secondaryFilters
operator|!=
literal|null
condition|)
block|{
comment|// OR logic
name|boolean
name|flag
init|=
literal|false
decl_stmt|;
for|for
control|(
name|NameValuePair
name|secondaryFilter
range|:
name|secondaryFilters
control|)
block|{
if|if
condition|(
name|secondaryFilter
operator|!=
literal|null
operator|&&
name|matchFilter
argument_list|(
name|entity
operator|.
name|getOtherInfo
argument_list|()
argument_list|,
name|secondaryFilter
argument_list|)
condition|)
block|{
name|flag
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|flag
condition|)
block|{
continue|continue;
block|}
block|}
name|entitiesSelected
operator|.
name|add
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ATSEntity
argument_list|>
name|entitiesToReturn
init|=
operator|new
name|ArrayList
argument_list|<
name|ATSEntity
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ATSEntity
name|entitySelected
range|:
name|entitiesSelected
control|)
block|{
name|entitiesToReturn
operator|.
name|add
argument_list|(
name|maskFields
argument_list|(
name|entitySelected
argument_list|,
name|fields
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|entitiesToReturn
argument_list|)
expr_stmt|;
name|ATSEntities
name|entitiesWrapper
init|=
operator|new
name|ATSEntities
argument_list|()
decl_stmt|;
name|entitiesWrapper
operator|.
name|setEntities
argument_list|(
name|entitiesToReturn
argument_list|)
expr_stmt|;
return|return
name|entitiesWrapper
return|;
block|}
annotation|@
name|Override
DECL|method|getEntity (String entityId, String entityType, EnumSet<Field> fieldsToRetrieve)
specifier|public
name|ATSEntity
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
block|{
if|if
condition|(
name|fieldsToRetrieve
operator|==
literal|null
condition|)
block|{
name|fieldsToRetrieve
operator|=
name|EnumSet
operator|.
name|allOf
argument_list|(
name|Field
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|ATSEntity
name|entity
init|=
name|entities
operator|.
name|get
argument_list|(
operator|new
name|EntityIdentifier
argument_list|(
name|entityId
argument_list|,
name|entityType
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|maskFields
argument_list|(
name|entity
argument_list|,
name|fieldsToRetrieve
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEntityTimelines (String entityType, SortedSet<String> entityIds, Long limit, Long windowStart, Long windowEnd, Set<String> eventTypes)
specifier|public
name|ATSEvents
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
block|{
name|ATSEvents
name|allEvents
init|=
operator|new
name|ATSEvents
argument_list|()
decl_stmt|;
if|if
condition|(
name|entityIds
operator|==
literal|null
condition|)
block|{
return|return
name|allEvents
return|;
block|}
if|if
condition|(
name|limit
operator|==
literal|null
condition|)
block|{
name|limit
operator|=
name|DEFAULT_LIMIT
expr_stmt|;
block|}
if|if
condition|(
name|windowStart
operator|==
literal|null
condition|)
block|{
name|windowStart
operator|=
name|Long
operator|.
name|MIN_VALUE
expr_stmt|;
block|}
if|if
condition|(
name|windowEnd
operator|==
literal|null
condition|)
block|{
name|windowEnd
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
for|for
control|(
name|String
name|entityId
range|:
name|entityIds
control|)
block|{
name|EntityIdentifier
name|entityID
init|=
operator|new
name|EntityIdentifier
argument_list|(
name|entityId
argument_list|,
name|entityType
argument_list|)
decl_stmt|;
name|ATSEntity
name|entity
init|=
name|entities
operator|.
name|get
argument_list|(
name|entityID
argument_list|)
decl_stmt|;
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ATSEventsOfOneEntity
name|events
init|=
operator|new
name|ATSEventsOfOneEntity
argument_list|()
decl_stmt|;
name|events
operator|.
name|setEntityId
argument_list|(
name|entityId
argument_list|)
expr_stmt|;
name|events
operator|.
name|setEntityType
argument_list|(
name|entityType
argument_list|)
expr_stmt|;
for|for
control|(
name|ATSEvent
name|event
range|:
name|entity
operator|.
name|getEvents
argument_list|()
control|)
block|{
if|if
condition|(
name|events
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
operator|>=
name|limit
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|event
operator|.
name|getTimestamp
argument_list|()
operator|<=
name|windowStart
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|event
operator|.
name|getTimestamp
argument_list|()
operator|>
name|windowEnd
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|eventTypes
operator|!=
literal|null
operator|&&
operator|!
name|eventTypes
operator|.
name|contains
argument_list|(
name|event
operator|.
name|getEventType
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|events
operator|.
name|addEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
name|allEvents
operator|.
name|addEvent
argument_list|(
name|events
argument_list|)
expr_stmt|;
block|}
return|return
name|allEvents
return|;
block|}
annotation|@
name|Override
DECL|method|put (ATSEntities data)
specifier|public
name|ATSPutErrors
name|put
parameter_list|(
name|ATSEntities
name|data
parameter_list|)
block|{
name|ATSPutErrors
name|errors
init|=
operator|new
name|ATSPutErrors
argument_list|()
decl_stmt|;
for|for
control|(
name|ATSEntity
name|entity
range|:
name|data
operator|.
name|getEntities
argument_list|()
control|)
block|{
name|EntityIdentifier
name|entityId
init|=
operator|new
name|EntityIdentifier
argument_list|(
name|entity
operator|.
name|getEntityId
argument_list|()
argument_list|,
name|entity
operator|.
name|getEntityType
argument_list|()
argument_list|)
decl_stmt|;
comment|// store entity info in memory
name|ATSEntity
name|existingEntity
init|=
name|entities
operator|.
name|get
argument_list|(
name|entityId
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingEntity
operator|==
literal|null
condition|)
block|{
name|existingEntity
operator|=
operator|new
name|ATSEntity
argument_list|()
expr_stmt|;
name|existingEntity
operator|.
name|setEntityId
argument_list|(
name|entity
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|existingEntity
operator|.
name|setEntityType
argument_list|(
name|entity
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|existingEntity
operator|.
name|setStartTime
argument_list|(
name|entity
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|entities
operator|.
name|put
argument_list|(
name|entityId
argument_list|,
name|existingEntity
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entity
operator|.
name|getEvents
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|existingEntity
operator|.
name|getEvents
argument_list|()
operator|==
literal|null
condition|)
block|{
name|existingEntity
operator|.
name|setEvents
argument_list|(
name|entity
operator|.
name|getEvents
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|existingEntity
operator|.
name|addEvents
argument_list|(
name|entity
operator|.
name|getEvents
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|existingEntity
operator|.
name|getEvents
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// check startTime
if|if
condition|(
name|existingEntity
operator|.
name|getStartTime
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|existingEntity
operator|.
name|getEvents
argument_list|()
operator|==
literal|null
operator|||
name|existingEntity
operator|.
name|getEvents
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ATSPutError
name|error
init|=
operator|new
name|ATSPutError
argument_list|()
decl_stmt|;
name|error
operator|.
name|setEntityId
argument_list|(
name|entityId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|error
operator|.
name|setEntityType
argument_list|(
name|entityId
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|error
operator|.
name|setErrorCode
argument_list|(
name|ATSPutError
operator|.
name|NO_START_TIME
argument_list|)
expr_stmt|;
name|errors
operator|.
name|addError
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|entities
operator|.
name|remove
argument_list|(
name|entityId
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|existingEntity
operator|.
name|setStartTime
argument_list|(
name|entity
operator|.
name|getEvents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|entity
operator|.
name|getPrimaryFilters
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|existingEntity
operator|.
name|getPrimaryFilters
argument_list|()
operator|==
literal|null
condition|)
block|{
name|existingEntity
operator|.
name|setPrimaryFilters
argument_list|(
name|entity
operator|.
name|getPrimaryFilters
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|existingEntity
operator|.
name|addPrimaryFilters
argument_list|(
name|entity
operator|.
name|getPrimaryFilters
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|entity
operator|.
name|getOtherInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|existingEntity
operator|.
name|getOtherInfo
argument_list|()
operator|==
literal|null
condition|)
block|{
name|existingEntity
operator|.
name|setOtherInfo
argument_list|(
name|entity
operator|.
name|getOtherInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|existingEntity
operator|.
name|addOtherInfo
argument_list|(
name|entity
operator|.
name|getOtherInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// relate it to other entities
if|if
condition|(
name|entity
operator|.
name|getRelatedEntities
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|partRelatedEntities
range|:
name|entity
operator|.
name|getRelatedEntities
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|partRelatedEntities
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|String
name|idStr
range|:
name|partRelatedEntities
operator|.
name|getValue
argument_list|()
control|)
block|{
name|EntityIdentifier
name|relatedEntityId
init|=
operator|new
name|EntityIdentifier
argument_list|(
name|idStr
argument_list|,
name|partRelatedEntities
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|ATSEntity
name|relatedEntity
init|=
name|entities
operator|.
name|get
argument_list|(
name|relatedEntityId
argument_list|)
decl_stmt|;
if|if
condition|(
name|relatedEntity
operator|!=
literal|null
condition|)
block|{
name|relatedEntity
operator|.
name|addRelatedEntity
argument_list|(
name|existingEntity
operator|.
name|getEntityType
argument_list|()
argument_list|,
name|existingEntity
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|relatedEntity
operator|=
operator|new
name|ATSEntity
argument_list|()
expr_stmt|;
name|relatedEntity
operator|.
name|setEntityId
argument_list|(
name|relatedEntityId
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|relatedEntity
operator|.
name|setEntityType
argument_list|(
name|relatedEntityId
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|relatedEntity
operator|.
name|setStartTime
argument_list|(
name|existingEntity
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|relatedEntity
operator|.
name|addRelatedEntity
argument_list|(
name|existingEntity
operator|.
name|getEntityType
argument_list|()
argument_list|,
name|existingEntity
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|entities
operator|.
name|put
argument_list|(
name|relatedEntityId
argument_list|,
name|relatedEntity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|errors
return|;
block|}
DECL|method|maskFields ( ATSEntity entity, EnumSet<Field> fields)
specifier|private
specifier|static
name|ATSEntity
name|maskFields
parameter_list|(
name|ATSEntity
name|entity
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
block|{
comment|// Conceal the fields that are not going to be exposed
name|ATSEntity
name|entityToReturn
init|=
operator|new
name|ATSEntity
argument_list|()
decl_stmt|;
name|entityToReturn
operator|.
name|setEntityId
argument_list|(
name|entity
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|entityToReturn
operator|.
name|setEntityType
argument_list|(
name|entity
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|entityToReturn
operator|.
name|setStartTime
argument_list|(
name|entity
operator|.
name|getStartTime
argument_list|()
argument_list|)
expr_stmt|;
name|entityToReturn
operator|.
name|setEvents
argument_list|(
name|fields
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|EVENTS
argument_list|)
condition|?
name|entity
operator|.
name|getEvents
argument_list|()
else|:
name|fields
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|LAST_EVENT_ONLY
argument_list|)
condition|?
name|Arrays
operator|.
name|asList
argument_list|(
name|entity
operator|.
name|getEvents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
else|:
literal|null
argument_list|)
expr_stmt|;
name|entityToReturn
operator|.
name|setRelatedEntities
argument_list|(
name|fields
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|RELATED_ENTITIES
argument_list|)
condition|?
name|entity
operator|.
name|getRelatedEntities
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
name|entityToReturn
operator|.
name|setPrimaryFilters
argument_list|(
name|fields
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|PRIMARY_FILTERS
argument_list|)
condition|?
name|entity
operator|.
name|getPrimaryFilters
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
name|entityToReturn
operator|.
name|setOtherInfo
argument_list|(
name|fields
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|OTHER_INFO
argument_list|)
condition|?
name|entity
operator|.
name|getOtherInfo
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
return|return
name|entityToReturn
return|;
block|}
DECL|method|matchFilter (Map<String, Object> tags, NameValuePair filter)
specifier|private
specifier|static
name|boolean
name|matchFilter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|tags
parameter_list|,
name|NameValuePair
name|filter
parameter_list|)
block|{
name|Object
name|value
init|=
name|tags
operator|.
name|get
argument_list|(
name|filter
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// doesn't have the filter
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
name|filter
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
comment|// doesn't match the filter
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|matchPrimaryFilter (Map<String, Set<Object>> tags, NameValuePair filter)
specifier|private
specifier|static
name|boolean
name|matchPrimaryFilter
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|tags
parameter_list|,
name|NameValuePair
name|filter
parameter_list|)
block|{
name|Set
argument_list|<
name|Object
argument_list|>
name|value
init|=
name|tags
operator|.
name|get
argument_list|(
name|filter
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// doesn't have the filter
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|value
operator|.
name|contains
argument_list|(
name|filter
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

