begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.timelineservice
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
name|timelineservice
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
name|util
operator|.
name|TimelineServiceHelper
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
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TimelineEntity
specifier|public
class|class
name|TimelineEntity
block|{
DECL|field|SYSTEM_INFO_KEY_PREFIX
specifier|protected
specifier|final
specifier|static
name|String
name|SYSTEM_INFO_KEY_PREFIX
init|=
literal|"SYSTEM_INFO_"
decl_stmt|;
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"identifier"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|NONE
argument_list|)
DECL|class|Identifier
specifier|public
specifier|static
class|class
name|Identifier
block|{
DECL|field|type
specifier|private
name|String
name|type
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|method|Identifier (String type, String id)
specifier|public
name|Identifier
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|Identifier ()
specifier|public
name|Identifier
parameter_list|()
block|{      }
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"type"
argument_list|)
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|setType (String type)
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"id"
argument_list|)
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|setId (String id)
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"TimelineEntity["
operator|+
literal|"type='"
operator|+
name|type
operator|+
literal|'\''
operator|+
literal|", id='"
operator|+
name|id
operator|+
literal|'\''
operator|+
literal|"]"
return|;
block|}
block|}
DECL|field|real
specifier|private
name|TimelineEntity
name|real
decl_stmt|;
DECL|field|identifier
specifier|private
name|Identifier
name|identifier
decl_stmt|;
DECL|field|info
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|configs
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|metrics
specifier|private
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|events
specifier|private
name|Set
argument_list|<
name|TimelineEvent
argument_list|>
name|events
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|isRelatedToEntities
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|isRelatedToEntities
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|relatesToEntities
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatesToEntities
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|createdTime
specifier|private
name|long
name|createdTime
decl_stmt|;
DECL|field|modifiedTime
specifier|private
name|long
name|modifiedTime
decl_stmt|;
DECL|method|TimelineEntity ()
specifier|public
name|TimelineEntity
parameter_list|()
block|{
name|identifier
operator|=
operator|new
name|Identifier
argument_list|()
expr_stmt|;
block|}
comment|/**    *<p>    * The constuctor is used to construct a proxy {@link TimelineEntity} or its    * subclass object from the real entity object that carries information.    *</p>    *    *<p>    * It is usually used in the case where we want to recover class polymorphism    * after deserializing the entity from its JSON form.    *</p>    * @param entity the real entity that carries information    */
DECL|method|TimelineEntity (TimelineEntity entity)
specifier|public
name|TimelineEntity
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|)
block|{
name|real
operator|=
name|entity
operator|.
name|getReal
argument_list|()
expr_stmt|;
block|}
DECL|method|TimelineEntity (String type)
specifier|protected
name|TimelineEntity
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|identifier
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"type"
argument_list|)
DECL|method|getType ()
specifier|public
name|String
name|getType
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|identifier
operator|.
name|type
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
DECL|method|setType (String type)
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|identifier
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"id"
argument_list|)
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|identifier
operator|.
name|id
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
DECL|method|setId (String id)
specifier|public
name|void
name|setId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|identifier
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getIdentifier ()
specifier|public
name|Identifier
name|getIdentifier
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|identifier
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getIdentifier
argument_list|()
return|;
block|}
block|}
DECL|method|setIdentifier (Identifier identifier)
specifier|public
name|void
name|setIdentifier
parameter_list|(
name|Identifier
name|identifier
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|identifier
operator|=
name|identifier
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setIdentifier
argument_list|(
name|identifier
argument_list|)
expr_stmt|;
block|}
block|}
comment|// required by JAXB
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"info"
argument_list|)
DECL|method|getInfoJAXB ()
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getInfoJAXB
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|info
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getInfoJAXB
argument_list|()
return|;
block|}
block|}
DECL|method|getInfo ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|getInfo
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|info
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getInfo
argument_list|()
return|;
block|}
block|}
DECL|method|setInfo (Map<String, Object> info)
specifier|public
name|void
name|setInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|info
operator|=
name|TimelineServiceHelper
operator|.
name|mapCastToHashMap
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addInfo (Map<String, Object> info)
specifier|public
name|void
name|addInfo
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|info
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|info
operator|.
name|putAll
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addInfo
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addInfo (String key, Object value)
specifier|public
name|void
name|addInfo
parameter_list|(
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|info
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addInfo
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|// required by JAXB
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"configs"
argument_list|)
DECL|method|getConfigsJAXB ()
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getConfigsJAXB
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|configs
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getConfigsJAXB
argument_list|()
return|;
block|}
block|}
DECL|method|getConfigs ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getConfigs
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|configs
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getConfigs
argument_list|()
return|;
block|}
block|}
DECL|method|setConfigs (Map<String, String> configs)
specifier|public
name|void
name|setConfigs
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configs
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|configs
operator|=
name|TimelineServiceHelper
operator|.
name|mapCastToHashMap
argument_list|(
name|configs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setConfigs
argument_list|(
name|configs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addConfigs (Map<String, String> configs)
specifier|public
name|void
name|addConfigs
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configs
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|configs
operator|.
name|putAll
argument_list|(
name|configs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addConfigs
argument_list|(
name|configs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addConfig (String key, String value)
specifier|public
name|void
name|addConfig
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|configs
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addConfig
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"metrics"
argument_list|)
DECL|method|getMetrics ()
specifier|public
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|getMetrics
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|metrics
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getMetrics
argument_list|()
return|;
block|}
block|}
DECL|method|setMetrics (Set<TimelineMetric> metrics)
specifier|public
name|void
name|setMetrics
parameter_list|(
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|metrics
operator|=
name|metrics
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setMetrics
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addMetrics (Set<TimelineMetric> metrics)
specifier|public
name|void
name|addMetrics
parameter_list|(
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|metrics
operator|.
name|addAll
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addMetrics
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addMetric (TimelineMetric metric)
specifier|public
name|void
name|addMetric
parameter_list|(
name|TimelineMetric
name|metric
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|metrics
operator|.
name|add
argument_list|(
name|metric
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addMetric
argument_list|(
name|metric
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"events"
argument_list|)
DECL|method|getEvents ()
specifier|public
name|Set
argument_list|<
name|TimelineEvent
argument_list|>
name|getEvents
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|events
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getEvents
argument_list|()
return|;
block|}
block|}
DECL|method|setEvents (Set<TimelineEvent> events)
specifier|public
name|void
name|setEvents
parameter_list|(
name|Set
argument_list|<
name|TimelineEvent
argument_list|>
name|events
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|events
operator|=
name|events
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setEvents
argument_list|(
name|events
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addEvents (Set<TimelineEvent> events)
specifier|public
name|void
name|addEvents
parameter_list|(
name|Set
argument_list|<
name|TimelineEvent
argument_list|>
name|events
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
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
else|else
block|{
name|real
operator|.
name|addEvents
argument_list|(
name|events
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addEvent (TimelineEvent event)
specifier|public
name|void
name|addEvent
parameter_list|(
name|TimelineEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|events
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getIsRelatedToEntities ()
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
name|getIsRelatedToEntities
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|isRelatedToEntities
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getIsRelatedToEntities
argument_list|()
return|;
block|}
block|}
comment|// required by JAXB
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"isrelatedto"
argument_list|)
DECL|method|getIsRelatedToEntitiesJAXB ()
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|getIsRelatedToEntitiesJAXB
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|isRelatedToEntities
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getIsRelatedToEntitiesJAXB
argument_list|()
return|;
block|}
block|}
DECL|method|setIsRelatedToEntities ( Map<String, Set<String>> isRelatedToEntities)
specifier|public
name|void
name|setIsRelatedToEntities
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
name|isRelatedToEntities
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|isRelatedToEntities
operator|=
name|TimelineServiceHelper
operator|.
name|mapCastToHashMap
argument_list|(
name|isRelatedToEntities
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setIsRelatedToEntities
argument_list|(
name|isRelatedToEntities
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addIsRelatedToEntities ( Map<String, Set<String>> isRelatedToEntities)
specifier|public
name|void
name|addIsRelatedToEntities
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
name|isRelatedToEntities
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
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
name|entry
range|:
name|isRelatedToEntities
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|this
operator|.
name|isRelatedToEntities
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|isRelatedToEntities
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|real
operator|.
name|addIsRelatedToEntities
argument_list|(
name|isRelatedToEntities
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addIsRelatedToEntity (String type, String id)
specifier|public
name|void
name|addIsRelatedToEntity
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|isRelatedToEntities
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|isRelatedToEntities
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addIsRelatedToEntity
argument_list|(
name|type
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
comment|// required by JAXB
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"relatesto"
argument_list|)
DECL|method|getRelatesToEntitiesJAXB ()
specifier|public
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|getRelatesToEntitiesJAXB
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|relatesToEntities
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getRelatesToEntitiesJAXB
argument_list|()
return|;
block|}
block|}
DECL|method|getRelatesToEntities ()
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
name|getRelatesToEntities
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|relatesToEntities
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getRelatesToEntities
argument_list|()
return|;
block|}
block|}
DECL|method|addRelatesToEntities (Map<String, Set<String>> relatesToEntities)
specifier|public
name|void
name|addRelatesToEntities
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
name|relatesToEntities
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
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
name|entry
range|:
name|relatesToEntities
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|this
operator|.
name|relatesToEntities
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|relatesToEntities
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|addAll
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|real
operator|.
name|addRelatesToEntities
argument_list|(
name|relatesToEntities
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addRelatesToEntity (String type, String id)
specifier|public
name|void
name|addRelatesToEntity
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
name|relatesToEntities
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
name|ids
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|relatesToEntities
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|ids
argument_list|)
expr_stmt|;
block|}
name|ids
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|addRelatesToEntity
argument_list|(
name|type
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setRelatesToEntities (Map<String, Set<String>> relatesToEntities)
specifier|public
name|void
name|setRelatesToEntities
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
name|relatesToEntities
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|relatesToEntities
operator|=
name|TimelineServiceHelper
operator|.
name|mapCastToHashMap
argument_list|(
name|relatesToEntities
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setRelatesToEntities
argument_list|(
name|relatesToEntities
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"createdtime"
argument_list|)
DECL|method|getCreatedTime ()
specifier|public
name|long
name|getCreatedTime
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|createdTime
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getCreatedTime
argument_list|()
return|;
block|}
block|}
DECL|method|setCreatedTime (long createdTime)
specifier|public
name|void
name|setCreatedTime
parameter_list|(
name|long
name|createdTime
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|createdTime
operator|=
name|createdTime
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setCreatedTime
argument_list|(
name|createdTime
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"modifiedtime"
argument_list|)
DECL|method|getModifiedTime ()
specifier|public
name|long
name|getModifiedTime
parameter_list|()
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
return|return
name|modifiedTime
return|;
block|}
else|else
block|{
return|return
name|real
operator|.
name|getModifiedTime
argument_list|()
return|;
block|}
block|}
DECL|method|setModifiedTime (long modifiedTime)
specifier|public
name|void
name|setModifiedTime
parameter_list|(
name|long
name|modifiedTime
parameter_list|)
block|{
if|if
condition|(
name|real
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|modifiedTime
operator|=
name|modifiedTime
expr_stmt|;
block|}
else|else
block|{
name|real
operator|.
name|setModifiedTime
argument_list|(
name|modifiedTime
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getReal ()
specifier|protected
name|TimelineEntity
name|getReal
parameter_list|()
block|{
return|return
name|real
operator|==
literal|null
condition|?
name|this
else|:
name|real
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|identifier
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

