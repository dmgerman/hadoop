begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.apptimeline
package|package
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
operator|.
name|Entry
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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

begin_comment
comment|/**  *<p>  * The class that contains the the meta information of some conceptual entity of  * an application and its related events. The entity can be an application, an  * application attempt, a container or whatever the user-defined object.  *</p>  *   *<p>  * Primary filters will be used to index the entities in  *<code>ApplicationTimelineStore</code>, such that users should carefully  * choose the information they want to store as the primary filters. The  * remaining can be stored as other information.  *</p>  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"entity"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|NONE
argument_list|)
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|ATSEntity
specifier|public
class|class
name|ATSEntity
implements|implements
name|Comparable
argument_list|<
name|ATSEntity
argument_list|>
block|{
DECL|field|entityType
specifier|private
name|String
name|entityType
decl_stmt|;
DECL|field|entityId
specifier|private
name|String
name|entityId
decl_stmt|;
DECL|field|startTime
specifier|private
name|Long
name|startTime
decl_stmt|;
DECL|field|events
specifier|private
name|List
argument_list|<
name|ATSEvent
argument_list|>
name|events
init|=
operator|new
name|ArrayList
argument_list|<
name|ATSEvent
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|relatedEntities
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatedEntities
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|primaryFilters
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|primaryFilters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|otherInfo
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherInfo
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ATSEntity ()
specifier|public
name|ATSEntity
parameter_list|()
block|{    }
comment|/**    * Get the entity type    *     * @return the entity type    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"entitytype"
argument_list|)
DECL|method|getEntityType ()
specifier|public
name|String
name|getEntityType
parameter_list|()
block|{
return|return
name|entityType
return|;
block|}
comment|/**    * Set the entity type    *     * @param entityType    *          the entity type    */
DECL|method|setEntityType (String entityType)
specifier|public
name|void
name|setEntityType
parameter_list|(
name|String
name|entityType
parameter_list|)
block|{
name|this
operator|.
name|entityType
operator|=
name|entityType
expr_stmt|;
block|}
comment|/**    * Get the entity Id    *     * @return the entity Id    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"entity"
argument_list|)
DECL|method|getEntityId ()
specifier|public
name|String
name|getEntityId
parameter_list|()
block|{
return|return
name|entityId
return|;
block|}
comment|/**    * Set the entity Id    *     * @param entityId    *          the entity Id    */
DECL|method|setEntityId (String entityId)
specifier|public
name|void
name|setEntityId
parameter_list|(
name|String
name|entityId
parameter_list|)
block|{
name|this
operator|.
name|entityId
operator|=
name|entityId
expr_stmt|;
block|}
comment|/**    * Get the start time of the entity    *     * @return the start time of the entity    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"starttime"
argument_list|)
DECL|method|getStartTime ()
specifier|public
name|Long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
comment|/**    * Set the start time of the entity    *     * @param startTime    *          the start time of the entity    */
DECL|method|setStartTime (Long startTime)
specifier|public
name|void
name|setStartTime
parameter_list|(
name|Long
name|startTime
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
block|}
comment|/**    * Get a list of events related to the entity    *     * @return a list of events related to the entity    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"events"
argument_list|)
DECL|method|getEvents ()
specifier|public
name|List
argument_list|<
name|ATSEvent
argument_list|>
name|getEvents
parameter_list|()
block|{
return|return
name|events
return|;
block|}
comment|/**    * Add a single event related to the entity to the existing event list    *     * @param event    *          a single event related to the entity    */
DECL|method|addEvent (ATSEvent event)
specifier|public
name|void
name|addEvent
parameter_list|(
name|ATSEvent
name|event
parameter_list|)
block|{
name|events
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a list of events related to the entity to the existing event list    *     * @param events    *          a list of events related to the entity    */
DECL|method|addEvents (List<ATSEvent> events)
specifier|public
name|void
name|addEvents
parameter_list|(
name|List
argument_list|<
name|ATSEvent
argument_list|>
name|events
parameter_list|)
block|{
name|this
operator|.
name|events
operator|.
name|addAll
argument_list|(
name|events
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the event list to the given list of events related to the entity    *     * @param events    *          events a list of events related to the entity    */
DECL|method|setEvents (List<ATSEvent> events)
specifier|public
name|void
name|setEvents
parameter_list|(
name|List
argument_list|<
name|ATSEvent
argument_list|>
name|events
parameter_list|)
block|{
name|this
operator|.
name|events
operator|=
name|events
expr_stmt|;
block|}
comment|/**    * Get the related entities    *     * @return the related entities    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"relatedentities"
argument_list|)
DECL|method|getRelatedEntities ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|getRelatedEntities
parameter_list|()
block|{
return|return
name|relatedEntities
return|;
block|}
comment|/**    * Add an entity to the existing related entity map    *     * @param entityType    *          the entity type    * @param entityId    *          the entity Id    */
DECL|method|addRelatedEntity (String entityType, String entityId)
specifier|public
name|void
name|addRelatedEntity
parameter_list|(
name|String
name|entityType
parameter_list|,
name|String
name|entityId
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|thisRelatedEntity
init|=
name|relatedEntities
operator|.
name|get
argument_list|(
name|entityType
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisRelatedEntity
operator|==
literal|null
condition|)
block|{
name|thisRelatedEntity
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|relatedEntities
operator|.
name|put
argument_list|(
name|entityType
argument_list|,
name|thisRelatedEntity
argument_list|)
expr_stmt|;
block|}
name|thisRelatedEntity
operator|.
name|add
argument_list|(
name|entityId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a map of related entities to the existing related entity map    *     * @param relatedEntities    *          a map of related entities    */
DECL|method|addRelatedEntities (Map<String, Set<String>> relatedEntities)
specifier|public
name|void
name|addRelatedEntities
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatedEntities
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatedEntity
range|:
name|relatedEntities
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|thisRelatedEntity
init|=
name|this
operator|.
name|relatedEntities
operator|.
name|get
argument_list|(
name|relatedEntity
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisRelatedEntity
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|relatedEntities
operator|.
name|put
argument_list|(
name|relatedEntity
operator|.
name|getKey
argument_list|()
argument_list|,
name|relatedEntity
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|thisRelatedEntity
operator|.
name|addAll
argument_list|(
name|relatedEntity
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Set the related entity map to the given map of related entities    *     * @param relatedEntities    *          a map of related entities    */
DECL|method|setRelatedEntities ( Map<String, Set<String>> relatedEntities)
specifier|public
name|void
name|setRelatedEntities
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatedEntities
parameter_list|)
block|{
name|this
operator|.
name|relatedEntities
operator|=
name|relatedEntities
expr_stmt|;
block|}
comment|/**    * Get the primary filters    *     * @return the primary filters    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"primaryfilters"
argument_list|)
DECL|method|getPrimaryFilters ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|getPrimaryFilters
parameter_list|()
block|{
return|return
name|primaryFilters
return|;
block|}
comment|/**    * Add a single piece of primary filter to the existing primary filter map    *     * @param key    *          the primary filter key    * @param value    *          the primary filter value    */
DECL|method|addPrimaryFilter (String key, Object value)
specifier|public
name|void
name|addPrimaryFilter
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|Set
argument_list|<
name|Object
argument_list|>
name|thisPrimaryFilter
init|=
name|primaryFilters
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisPrimaryFilter
operator|==
literal|null
condition|)
block|{
name|thisPrimaryFilter
operator|=
operator|new
name|HashSet
argument_list|<
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|primaryFilters
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|thisPrimaryFilter
argument_list|)
expr_stmt|;
block|}
name|thisPrimaryFilter
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a map of primary filters to the existing primary filter map    *     * @param primaryFilters    *          a map of primary filters    */
DECL|method|addPrimaryFilters (Map<String, Set<Object>> primaryFilters)
specifier|public
name|void
name|addPrimaryFilters
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
name|primaryFilters
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|primaryFilter
range|:
name|primaryFilters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|Object
argument_list|>
name|thisPrimaryFilter
init|=
name|this
operator|.
name|primaryFilters
operator|.
name|get
argument_list|(
name|primaryFilter
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisPrimaryFilter
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|primaryFilters
operator|.
name|put
argument_list|(
name|primaryFilter
operator|.
name|getKey
argument_list|()
argument_list|,
name|primaryFilter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|thisPrimaryFilter
operator|.
name|addAll
argument_list|(
name|primaryFilter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Set the primary filter map to the given map of primary filters    *     * @param primaryFilters    *          a map of primary filters    */
DECL|method|setPrimaryFilters (Map<String, Set<Object>> primaryFilters)
specifier|public
name|void
name|setPrimaryFilters
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
name|primaryFilters
parameter_list|)
block|{
name|this
operator|.
name|primaryFilters
operator|=
name|primaryFilters
expr_stmt|;
block|}
comment|/**    * Get the other information of the entity    *     * @return the other information of the entity    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"otherinfo"
argument_list|)
DECL|method|getOtherInfo ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getOtherInfo
parameter_list|()
block|{
return|return
name|otherInfo
return|;
block|}
comment|/**    * Add one piece of other information of the entity to the existing other info    * map    *     * @param key    *          the other information key    * @param value    *          the other information value    */
DECL|method|addOtherInfo (String key, Object value)
specifier|public
name|void
name|addOtherInfo
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|otherInfo
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a map of other information of the entity to the existing other info map    *     * @param otherInfo    *          a map of other information    */
DECL|method|addOtherInfo (Map<String, Object> otherInfo)
specifier|public
name|void
name|addOtherInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherInfo
parameter_list|)
block|{
name|this
operator|.
name|otherInfo
operator|.
name|putAll
argument_list|(
name|otherInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the other info map to the given map of other information    *     * @param otherInfo    *          a map of other information    */
DECL|method|setOtherInfo (Map<String, Object> otherInfo)
specifier|public
name|void
name|setOtherInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherInfo
parameter_list|)
block|{
name|this
operator|.
name|otherInfo
operator|=
name|otherInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// generated by eclipse
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|entityId
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|entityId
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|entityType
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|entityType
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|events
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|events
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|otherInfo
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|otherInfo
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|primaryFilters
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|primaryFilters
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|relatedEntities
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|relatedEntities
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|startTime
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|startTime
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
comment|// generated by eclipse
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|ATSEntity
name|other
init|=
operator|(
name|ATSEntity
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|entityId
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|entityId
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|entityId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|entityId
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|entityType
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|entityType
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|entityType
operator|.
name|equals
argument_list|(
name|other
operator|.
name|entityType
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|events
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|events
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|events
operator|.
name|equals
argument_list|(
name|other
operator|.
name|events
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|otherInfo
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|otherInfo
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|otherInfo
operator|.
name|equals
argument_list|(
name|other
operator|.
name|otherInfo
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|primaryFilters
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|primaryFilters
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|primaryFilters
operator|.
name|equals
argument_list|(
name|other
operator|.
name|primaryFilters
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|relatedEntities
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|relatedEntities
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|relatedEntities
operator|.
name|equals
argument_list|(
name|other
operator|.
name|relatedEntities
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|startTime
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|startTime
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|startTime
operator|.
name|equals
argument_list|(
name|other
operator|.
name|startTime
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ATSEntity other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ATSEntity
name|other
parameter_list|)
block|{
name|int
name|comparison
init|=
name|entityType
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|entityType
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparison
operator|==
literal|0
condition|)
block|{
name|long
name|thisStartTime
init|=
name|startTime
operator|==
literal|null
condition|?
name|Long
operator|.
name|MIN_VALUE
else|:
name|startTime
decl_stmt|;
name|long
name|otherStartTime
init|=
name|other
operator|.
name|startTime
operator|==
literal|null
condition|?
name|Long
operator|.
name|MIN_VALUE
else|:
name|other
operator|.
name|startTime
decl_stmt|;
if|if
condition|(
name|thisStartTime
operator|>
name|otherStartTime
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|thisStartTime
operator|<
name|otherStartTime
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
name|entityId
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|entityId
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|comparison
return|;
block|}
block|}
block|}
end_class

end_unit

