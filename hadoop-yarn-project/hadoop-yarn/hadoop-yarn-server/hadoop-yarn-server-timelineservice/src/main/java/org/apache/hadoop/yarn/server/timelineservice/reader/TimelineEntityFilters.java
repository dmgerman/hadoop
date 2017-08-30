begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.reader
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
name|reader
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
name|TimelineCompareFilter
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
name|TimelineCompareOp
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
name|TimelineKeyValueFilter
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
name|TimelineExistsFilter
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
name|TimelineKeyValuesFilter
import|;
end_import

begin_comment
comment|/**  * Encapsulates information regarding the filters to apply while querying. These  * filters restrict the number of entities to return.<br>  * Filters contain the following :<br>  *<ul>  *<li><b>limit</b> - A limit on the number of entities to return. If null or  * {@literal< 0}, defaults to {@link #DEFAULT_LIMIT}. The maximum possible  * value for limit can be {@link Long#MAX_VALUE}.</li>  *<li><b>createdTimeBegin</b> - Matched entities should not be created before  * this timestamp. If null or {@literal<=0}, defaults to 0.</li>  *<li><b>createdTimeEnd</b> - Matched entities should not be created after this  * timestamp. If null or {@literal<=0}, defaults to  * {@link Long#MAX_VALUE}.</li>  *<li><b>relatesTo</b> - Matched entities should or should not relate to given  * entities depending on what's specified in the filter. The entities in  * relatesTo are identified by entity type and id. This is represented as  * a {@link TimelineFilterList} object containing  * {@link TimelineKeyValuesFilter} objects, each of which contains a  * set of values for a key and the comparison operator (equals/not equals). The  * key which represents the entity type is a string and values are a set of  * entity identifiers (also string). As it is a filter list, relatesTo can be  * evaluated with logical AND/OR and we can create a hierarchy of these  * {@link TimelineKeyValuesFilter} objects. If null or empty, the relations are  * not matched.</li>  *<li><b>isRelatedTo</b> - Matched entities should or should not be related  * to given entities depending on what's specified in the filter. The entities  * in isRelatedTo are identified by entity type and id.  This is represented as  * a {@link TimelineFilterList} object containing  * {@link TimelineKeyValuesFilter} objects, each of which contains a  * set of values for a key and the comparison operator (equals/not equals). The  * key which represents the entity type is a string and values are a set of  * entity identifiers (also string). As it is a filter list, relatesTo can be  * evaluated with logical AND/OR and we can create a hierarchy of these  * {@link TimelineKeyValuesFilter} objects. If null or empty, the relations are  * not matched.</li>  *<li><b>infoFilters</b> - Matched entities should have exact matches to  * the given info and should be either equal or not equal to given value  * depending on what's specified in the filter. This is represented as a  * {@link TimelineFilterList} object containing {@link TimelineKeyValueFilter}  * objects, each of which contains key-value pairs with a comparison operator  * (equals/not equals). The key which represents the info key is a string but  * value can be any object. As it is a filter list, info filters can be  * evaluated with logical AND/OR and we can create a hierarchy of these  * key-value pairs. If null or empty, the filter is not applied.</li>  *<li><b>configFilters</b> - Matched entities should have exact matches to  * the given configurations and should be either equal or not equal to given  * value depending on what's specified in the filter. This is represented as a  * {@link TimelineFilterList} object containing {@link TimelineKeyValueFilter}  * objects, each of which contains key-value pairs with a comparison operator  * (equals/not equals). Both key (which represents config name) and value (which  * is config value) are strings. As it is a filter list, config filters can be  * evaluated with logical AND/OR and we can create a hierarchy of these  * {@link TimelineKeyValueFilter} objects. If null or empty, the filter is not  * applied.</li>  *<li><b>metricFilters</b> - Matched entities should contain the given  * metrics and satisfy the specified relation with the value. This is  * represented as a {@link TimelineFilterList} object containing  * {@link TimelineCompareFilter} objects, each of which contains key-value pairs  * along with the specified relational/comparison operator represented by  * {@link TimelineCompareOp}.  The key is a string and value is integer  * (Short/Integer/Long). As it is a filter list, metric filters can be evaluated  * with logical AND/OR and we can create a hierarchy of these  * {@link TimelineCompareFilter} objects. If null or empty, the filter is not  * applied.</li>  *<li><b>eventFilters</b> - Matched entities should contain or not contain the  * given events. This is represented as a {@link TimelineFilterList} object  * containing {@link TimelineExistsFilter} objects, each of which contains a  * value which must or must not exist depending on comparison operator specified  * in the filter. For event filters, the value represents a event id. As it is a  * filter list, event filters can be evaluated with logical AND/OR and we can  * create a hierarchy of these {@link TimelineExistsFilter} objects. If null or  * empty, the filter is not applied.</li>  *<li><b>fromId</b> - If specified, retrieve the next set of entities from the  * given fromId. The set of entities retrieved is inclusive of specified fromId.  * fromId should be taken from the value associated with FROM_ID info key in  * entity response which was sent earlier.</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineEntityFilters
specifier|public
specifier|final
class|class
name|TimelineEntityFilters
block|{
DECL|field|limit
specifier|private
specifier|final
name|long
name|limit
decl_stmt|;
DECL|field|createdTimeBegin
specifier|private
name|long
name|createdTimeBegin
decl_stmt|;
DECL|field|createdTimeEnd
specifier|private
name|long
name|createdTimeEnd
decl_stmt|;
DECL|field|relatesTo
specifier|private
specifier|final
name|TimelineFilterList
name|relatesTo
decl_stmt|;
DECL|field|isRelatedTo
specifier|private
specifier|final
name|TimelineFilterList
name|isRelatedTo
decl_stmt|;
DECL|field|infoFilters
specifier|private
specifier|final
name|TimelineFilterList
name|infoFilters
decl_stmt|;
DECL|field|configFilters
specifier|private
specifier|final
name|TimelineFilterList
name|configFilters
decl_stmt|;
DECL|field|metricFilters
specifier|private
specifier|final
name|TimelineFilterList
name|metricFilters
decl_stmt|;
DECL|field|eventFilters
specifier|private
specifier|final
name|TimelineFilterList
name|eventFilters
decl_stmt|;
DECL|field|fromId
specifier|private
specifier|final
name|String
name|fromId
decl_stmt|;
DECL|field|DEFAULT_BEGIN_TIME
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_BEGIN_TIME
init|=
literal|0L
decl_stmt|;
DECL|field|DEFAULT_END_TIME
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_END_TIME
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
comment|/**    * Default limit of number of entities to return for getEntities API.    */
DECL|field|DEFAULT_LIMIT
specifier|public
specifier|static
specifier|final
name|long
name|DEFAULT_LIMIT
init|=
literal|100
decl_stmt|;
DECL|method|TimelineEntityFilters ( Long entityLimit, Long timeBegin, Long timeEnd, TimelineFilterList entityRelatesTo, TimelineFilterList entityIsRelatedTo, TimelineFilterList entityInfoFilters, TimelineFilterList entityConfigFilters, TimelineFilterList entityMetricFilters, TimelineFilterList entityEventFilters, String fromId)
specifier|private
name|TimelineEntityFilters
parameter_list|(
name|Long
name|entityLimit
parameter_list|,
name|Long
name|timeBegin
parameter_list|,
name|Long
name|timeEnd
parameter_list|,
name|TimelineFilterList
name|entityRelatesTo
parameter_list|,
name|TimelineFilterList
name|entityIsRelatedTo
parameter_list|,
name|TimelineFilterList
name|entityInfoFilters
parameter_list|,
name|TimelineFilterList
name|entityConfigFilters
parameter_list|,
name|TimelineFilterList
name|entityMetricFilters
parameter_list|,
name|TimelineFilterList
name|entityEventFilters
parameter_list|,
name|String
name|fromId
parameter_list|)
block|{
if|if
condition|(
name|entityLimit
operator|==
literal|null
operator|||
name|entityLimit
operator|<
literal|0
condition|)
block|{
name|this
operator|.
name|limit
operator|=
name|DEFAULT_LIMIT
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|limit
operator|=
name|entityLimit
expr_stmt|;
block|}
if|if
condition|(
name|timeBegin
operator|==
literal|null
operator|||
name|timeBegin
operator|<
literal|0
condition|)
block|{
name|this
operator|.
name|createdTimeBegin
operator|=
name|DEFAULT_BEGIN_TIME
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|createdTimeBegin
operator|=
name|timeBegin
expr_stmt|;
block|}
if|if
condition|(
name|timeEnd
operator|==
literal|null
operator|||
name|timeEnd
operator|<
literal|0
condition|)
block|{
name|this
operator|.
name|createdTimeEnd
operator|=
name|DEFAULT_END_TIME
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|createdTimeEnd
operator|=
name|timeEnd
expr_stmt|;
block|}
name|this
operator|.
name|relatesTo
operator|=
name|entityRelatesTo
expr_stmt|;
name|this
operator|.
name|isRelatedTo
operator|=
name|entityIsRelatedTo
expr_stmt|;
name|this
operator|.
name|infoFilters
operator|=
name|entityInfoFilters
expr_stmt|;
name|this
operator|.
name|configFilters
operator|=
name|entityConfigFilters
expr_stmt|;
name|this
operator|.
name|metricFilters
operator|=
name|entityMetricFilters
expr_stmt|;
name|this
operator|.
name|eventFilters
operator|=
name|entityEventFilters
expr_stmt|;
name|this
operator|.
name|fromId
operator|=
name|fromId
expr_stmt|;
block|}
DECL|method|getLimit ()
specifier|public
name|long
name|getLimit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
DECL|method|getCreatedTimeBegin ()
specifier|public
name|long
name|getCreatedTimeBegin
parameter_list|()
block|{
return|return
name|createdTimeBegin
return|;
block|}
DECL|method|getCreatedTimeEnd ()
specifier|public
name|long
name|getCreatedTimeEnd
parameter_list|()
block|{
return|return
name|createdTimeEnd
return|;
block|}
DECL|method|getRelatesTo ()
specifier|public
name|TimelineFilterList
name|getRelatesTo
parameter_list|()
block|{
return|return
name|relatesTo
return|;
block|}
DECL|method|getIsRelatedTo ()
specifier|public
name|TimelineFilterList
name|getIsRelatedTo
parameter_list|()
block|{
return|return
name|isRelatedTo
return|;
block|}
DECL|method|getInfoFilters ()
specifier|public
name|TimelineFilterList
name|getInfoFilters
parameter_list|()
block|{
return|return
name|infoFilters
return|;
block|}
DECL|method|getConfigFilters ()
specifier|public
name|TimelineFilterList
name|getConfigFilters
parameter_list|()
block|{
return|return
name|configFilters
return|;
block|}
DECL|method|getMetricFilters ()
specifier|public
name|TimelineFilterList
name|getMetricFilters
parameter_list|()
block|{
return|return
name|metricFilters
return|;
block|}
DECL|method|getEventFilters ()
specifier|public
name|TimelineFilterList
name|getEventFilters
parameter_list|()
block|{
return|return
name|eventFilters
return|;
block|}
DECL|method|getFromId ()
specifier|public
name|String
name|getFromId
parameter_list|()
block|{
return|return
name|fromId
return|;
block|}
comment|/**    * A builder class to build an instance of TimelineEntityFilters.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|entityLimit
specifier|private
name|Long
name|entityLimit
decl_stmt|;
DECL|field|createdTimeBegin
specifier|private
name|Long
name|createdTimeBegin
decl_stmt|;
DECL|field|createdTimeEnd
specifier|private
name|Long
name|createdTimeEnd
decl_stmt|;
DECL|field|relatesToFilters
specifier|private
name|TimelineFilterList
name|relatesToFilters
decl_stmt|;
DECL|field|isRelatedToFilters
specifier|private
name|TimelineFilterList
name|isRelatedToFilters
decl_stmt|;
DECL|field|entityInfoFilters
specifier|private
name|TimelineFilterList
name|entityInfoFilters
decl_stmt|;
DECL|field|entityConfigFilters
specifier|private
name|TimelineFilterList
name|entityConfigFilters
decl_stmt|;
DECL|field|entityMetricFilters
specifier|private
name|TimelineFilterList
name|entityMetricFilters
decl_stmt|;
DECL|field|entityEventFilters
specifier|private
name|TimelineFilterList
name|entityEventFilters
decl_stmt|;
DECL|field|entityFromId
specifier|private
name|String
name|entityFromId
decl_stmt|;
DECL|method|entityLimit (Long limit)
specifier|public
name|Builder
name|entityLimit
parameter_list|(
name|Long
name|limit
parameter_list|)
block|{
name|this
operator|.
name|entityLimit
operator|=
name|limit
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|createdTimeBegin (Long timeBegin)
specifier|public
name|Builder
name|createdTimeBegin
parameter_list|(
name|Long
name|timeBegin
parameter_list|)
block|{
name|this
operator|.
name|createdTimeBegin
operator|=
name|timeBegin
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|createTimeEnd (Long timeEnd)
specifier|public
name|Builder
name|createTimeEnd
parameter_list|(
name|Long
name|timeEnd
parameter_list|)
block|{
name|this
operator|.
name|createdTimeEnd
operator|=
name|timeEnd
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|relatesTo (TimelineFilterList relatesTo)
specifier|public
name|Builder
name|relatesTo
parameter_list|(
name|TimelineFilterList
name|relatesTo
parameter_list|)
block|{
name|this
operator|.
name|relatesToFilters
operator|=
name|relatesTo
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|isRelatedTo (TimelineFilterList isRelatedTo)
specifier|public
name|Builder
name|isRelatedTo
parameter_list|(
name|TimelineFilterList
name|isRelatedTo
parameter_list|)
block|{
name|this
operator|.
name|isRelatedToFilters
operator|=
name|isRelatedTo
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|infoFilters (TimelineFilterList infoFilters)
specifier|public
name|Builder
name|infoFilters
parameter_list|(
name|TimelineFilterList
name|infoFilters
parameter_list|)
block|{
name|this
operator|.
name|entityInfoFilters
operator|=
name|infoFilters
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|configFilters (TimelineFilterList configFilters)
specifier|public
name|Builder
name|configFilters
parameter_list|(
name|TimelineFilterList
name|configFilters
parameter_list|)
block|{
name|this
operator|.
name|entityConfigFilters
operator|=
name|configFilters
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|metricFilters (TimelineFilterList metricFilters)
specifier|public
name|Builder
name|metricFilters
parameter_list|(
name|TimelineFilterList
name|metricFilters
parameter_list|)
block|{
name|this
operator|.
name|entityMetricFilters
operator|=
name|metricFilters
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|eventFilters (TimelineFilterList eventFilters)
specifier|public
name|Builder
name|eventFilters
parameter_list|(
name|TimelineFilterList
name|eventFilters
parameter_list|)
block|{
name|this
operator|.
name|entityEventFilters
operator|=
name|eventFilters
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fromId (String fromId)
specifier|public
name|Builder
name|fromId
parameter_list|(
name|String
name|fromId
parameter_list|)
block|{
name|this
operator|.
name|entityFromId
operator|=
name|fromId
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|TimelineEntityFilters
name|build
parameter_list|()
block|{
return|return
operator|new
name|TimelineEntityFilters
argument_list|(
name|entityLimit
argument_list|,
name|createdTimeBegin
argument_list|,
name|createdTimeEnd
argument_list|,
name|relatesToFilters
argument_list|,
name|isRelatedToFilters
argument_list|,
name|entityInfoFilters
argument_list|,
name|entityConfigFilters
argument_list|,
name|entityMetricFilters
argument_list|,
name|entityEventFilters
argument_list|,
name|entityFromId
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

