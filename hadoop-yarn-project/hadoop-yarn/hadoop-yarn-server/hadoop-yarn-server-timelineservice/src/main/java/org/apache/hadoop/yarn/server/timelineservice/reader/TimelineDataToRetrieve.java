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
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|storage
operator|.
name|TimelineReader
operator|.
name|Field
import|;
end_import

begin_comment
comment|/**  * Encapsulates information regarding which data to retrieve for each entity  * while querying.<br>  * Data to retrieve contains the following :<br>  *<ul>  *<li><b>confsToRetrieve</b> - Used for deciding which configs to return  * in response. This is represented as a {@link TimelineFilterList} object  * containing {@link TimelinePrefixFilter} objects. These can either be  * exact config keys' or prefixes which are then compared against config  * keys' to decide configs(inside entities) to return in response. If null  * or empty, all configurations will be fetched if fieldsToRetrieve  * contains {@link Field#CONFIGS} or {@link Field#ALL}. This should not be  * confused with configFilters which is used to decide which entities to  * return instead.</li>  *<li><b>metricsToRetrieve</b> - Used for deciding which metrics to return  * in response. This is represented as a {@link TimelineFilterList} object  * containing {@link TimelinePrefixFilter} objects. These can either be  * exact metric ids' or prefixes which are then compared against metric  * ids' to decide metrics(inside entities) to return in response. If null  * or empty, all metrics will be fetched if fieldsToRetrieve contains  * {@link Field#METRICS} or {@link Field#ALL}. This should not be confused  * with metricFilters which is used to decide which entities to return  * instead.</li>  *<li><b>fieldsToRetrieve</b> - Specifies which fields of the entity  * object to retrieve, see {@link Field}. If null, retrieves 3 fields,  * namely entity id, entity type and entity created time. All fields will  * be returned if {@link Field#ALL} is specified.</li>  *<li><b>metricsLimit</b> - If fieldsToRetrieve contains METRICS/ALL or  * metricsToRetrieve is specified, this limit defines an upper limit to the  * number of metrics to return. This parameter is ignored if METRICS are not to  * be fetched.</li>  *</ul>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineDataToRetrieve
specifier|public
class|class
name|TimelineDataToRetrieve
block|{
DECL|field|confsToRetrieve
specifier|private
name|TimelineFilterList
name|confsToRetrieve
decl_stmt|;
DECL|field|metricsToRetrieve
specifier|private
name|TimelineFilterList
name|metricsToRetrieve
decl_stmt|;
DECL|field|fieldsToRetrieve
specifier|private
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
decl_stmt|;
DECL|field|metricsLimit
specifier|private
name|Integer
name|metricsLimit
decl_stmt|;
comment|/**    * Default limit of number of metrics to return.    */
DECL|field|DEFAULT_METRICS_LIMIT
specifier|public
specifier|static
specifier|final
name|Integer
name|DEFAULT_METRICS_LIMIT
init|=
literal|1
decl_stmt|;
DECL|method|TimelineDataToRetrieve ()
specifier|public
name|TimelineDataToRetrieve
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TimelineDataToRetrieve (TimelineFilterList confs, TimelineFilterList metrics, EnumSet<Field> fields, Integer limitForMetrics)
specifier|public
name|TimelineDataToRetrieve
parameter_list|(
name|TimelineFilterList
name|confs
parameter_list|,
name|TimelineFilterList
name|metrics
parameter_list|,
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|,
name|Integer
name|limitForMetrics
parameter_list|)
block|{
name|this
operator|.
name|confsToRetrieve
operator|=
name|confs
expr_stmt|;
name|this
operator|.
name|metricsToRetrieve
operator|=
name|metrics
expr_stmt|;
name|this
operator|.
name|fieldsToRetrieve
operator|=
name|fields
expr_stmt|;
if|if
condition|(
name|limitForMetrics
operator|==
literal|null
operator|||
name|limitForMetrics
operator|<
literal|1
condition|)
block|{
name|this
operator|.
name|metricsLimit
operator|=
name|DEFAULT_METRICS_LIMIT
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|metricsLimit
operator|=
name|limitForMetrics
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|fieldsToRetrieve
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|fieldsToRetrieve
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Field
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getConfsToRetrieve ()
specifier|public
name|TimelineFilterList
name|getConfsToRetrieve
parameter_list|()
block|{
return|return
name|confsToRetrieve
return|;
block|}
DECL|method|setConfsToRetrieve (TimelineFilterList confs)
specifier|public
name|void
name|setConfsToRetrieve
parameter_list|(
name|TimelineFilterList
name|confs
parameter_list|)
block|{
name|this
operator|.
name|confsToRetrieve
operator|=
name|confs
expr_stmt|;
block|}
DECL|method|getMetricsToRetrieve ()
specifier|public
name|TimelineFilterList
name|getMetricsToRetrieve
parameter_list|()
block|{
return|return
name|metricsToRetrieve
return|;
block|}
DECL|method|setMetricsToRetrieve (TimelineFilterList metrics)
specifier|public
name|void
name|setMetricsToRetrieve
parameter_list|(
name|TimelineFilterList
name|metrics
parameter_list|)
block|{
name|this
operator|.
name|metricsToRetrieve
operator|=
name|metrics
expr_stmt|;
block|}
DECL|method|getFieldsToRetrieve ()
specifier|public
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|getFieldsToRetrieve
parameter_list|()
block|{
return|return
name|fieldsToRetrieve
return|;
block|}
DECL|method|setFieldsToRetrieve (EnumSet<Field> fields)
specifier|public
name|void
name|setFieldsToRetrieve
parameter_list|(
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fieldsToRetrieve
operator|=
name|fields
expr_stmt|;
block|}
comment|/**    * Adds configs and metrics fields to fieldsToRetrieve(if they are not    * present) if confsToRetrieve and metricsToRetrieve are specified.    */
DECL|method|addFieldsBasedOnConfsAndMetricsToRetrieve ()
specifier|public
name|void
name|addFieldsBasedOnConfsAndMetricsToRetrieve
parameter_list|()
block|{
if|if
condition|(
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|CONFIGS
argument_list|)
operator|&&
name|confsToRetrieve
operator|!=
literal|null
operator|&&
operator|!
name|confsToRetrieve
operator|.
name|getFilterList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fieldsToRetrieve
operator|.
name|add
argument_list|(
name|Field
operator|.
name|CONFIGS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|METRICS
argument_list|)
operator|&&
name|metricsToRetrieve
operator|!=
literal|null
operator|&&
operator|!
name|metricsToRetrieve
operator|.
name|getFilterList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fieldsToRetrieve
operator|.
name|add
argument_list|(
name|Field
operator|.
name|METRICS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMetricsLimit ()
specifier|public
name|Integer
name|getMetricsLimit
parameter_list|()
block|{
return|return
name|metricsLimit
return|;
block|}
DECL|method|setMetricsLimit (Integer limit)
specifier|public
name|void
name|setMetricsLimit
parameter_list|(
name|Integer
name|limit
parameter_list|)
block|{
if|if
condition|(
name|limit
operator|==
literal|null
operator|||
name|limit
operator|<
literal|1
condition|)
block|{
name|this
operator|.
name|metricsLimit
operator|=
name|DEFAULT_METRICS_LIMIT
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|metricsLimit
operator|=
name|limit
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

