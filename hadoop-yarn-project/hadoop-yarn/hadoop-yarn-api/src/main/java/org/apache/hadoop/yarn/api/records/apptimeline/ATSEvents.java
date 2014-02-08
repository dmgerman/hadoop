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
name|List
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
comment|/**  * The class that hosts a list of events, which are categorized according to  * their related entities.  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"events"
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
DECL|class|ATSEvents
specifier|public
class|class
name|ATSEvents
block|{
DECL|field|allEvents
specifier|private
name|List
argument_list|<
name|ATSEventsOfOneEntity
argument_list|>
name|allEvents
init|=
operator|new
name|ArrayList
argument_list|<
name|ATSEventsOfOneEntity
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ATSEvents ()
specifier|public
name|ATSEvents
parameter_list|()
block|{    }
comment|/**    * Get a list of {@link ATSEventsOfOneEntity} instances    *     * @return a list of {@link ATSEventsOfOneEntity} instances    */
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"events"
argument_list|)
DECL|method|getAllEvents ()
specifier|public
name|List
argument_list|<
name|ATSEventsOfOneEntity
argument_list|>
name|getAllEvents
parameter_list|()
block|{
return|return
name|allEvents
return|;
block|}
comment|/**    * Add a single {@link ATSEventsOfOneEntity} instance into the existing list    *     * @param eventsOfOneEntity    *          a single {@link ATSEventsOfOneEntity} instance    */
DECL|method|addEvent (ATSEventsOfOneEntity eventsOfOneEntity)
specifier|public
name|void
name|addEvent
parameter_list|(
name|ATSEventsOfOneEntity
name|eventsOfOneEntity
parameter_list|)
block|{
name|allEvents
operator|.
name|add
argument_list|(
name|eventsOfOneEntity
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a list of {@link ATSEventsOfOneEntity} instances into the existing list    *     * @param allEvents    *          a list of {@link ATSEventsOfOneEntity} instances    */
DECL|method|addEvents (List<ATSEventsOfOneEntity> allEvents)
specifier|public
name|void
name|addEvents
parameter_list|(
name|List
argument_list|<
name|ATSEventsOfOneEntity
argument_list|>
name|allEvents
parameter_list|)
block|{
name|this
operator|.
name|allEvents
operator|.
name|addAll
argument_list|(
name|allEvents
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the list to the given list of {@link ATSEventsOfOneEntity} instances    *     * @param allEvents    *          a list of {@link ATSEventsOfOneEntity} instances    */
DECL|method|setEvents (List<ATSEventsOfOneEntity> allEvents)
specifier|public
name|void
name|setEvents
parameter_list|(
name|List
argument_list|<
name|ATSEventsOfOneEntity
argument_list|>
name|allEvents
parameter_list|)
block|{
name|this
operator|.
name|allEvents
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|allEvents
operator|.
name|addAll
argument_list|(
name|allEvents
argument_list|)
expr_stmt|;
block|}
comment|/**    * The class that hosts a list of events that are only related to one entity.    */
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"events"
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
DECL|class|ATSEventsOfOneEntity
specifier|public
specifier|static
class|class
name|ATSEventsOfOneEntity
block|{
DECL|field|entityId
specifier|private
name|String
name|entityId
decl_stmt|;
DECL|field|entityType
specifier|private
name|String
name|entityType
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
DECL|method|ATSEventsOfOneEntity ()
specifier|public
name|ATSEventsOfOneEntity
parameter_list|()
block|{      }
comment|/**      * Get the entity Id      *       * @return the entity Id      */
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
comment|/**      * Set the entity Id      *       * @param entityId      *          the entity Id      */
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
comment|/**      * Get the entity type      *       * @return the entity type      */
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
comment|/**      * Set the entity type      *       * @param entityType      *          the entity type      */
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
comment|/**      * Get a list of events      *       * @return a list of events      */
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
comment|/**      * Add a single event to the existing event list      *       * @param event      *          a single event      */
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
comment|/**      * Add a list of event to the existing event list      *       * @param events      *          a list of events      */
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
comment|/**      * Set the event list to the given list of events      *       * @param events      *          a list of events      */
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
block|}
block|}
end_class

end_unit

