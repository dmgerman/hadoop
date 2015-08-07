begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
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
operator|.
name|common
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
name|timelineservice
operator|.
name|TimelineEvent
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
name|TimelineMetric
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
DECL|class|TimelineReaderUtils
specifier|public
class|class
name|TimelineReaderUtils
block|{
comment|/**    *    * @param entityRelations the relations of an entity    * @param relationFilters the relations for filtering    * @return a boolean flag to indicate if both match    */
DECL|method|matchRelations ( Map<String, Set<String>> entityRelations, Map<String, Set<String>> relationFilters)
specifier|public
specifier|static
name|boolean
name|matchRelations
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
name|entityRelations
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
name|relationFilters
parameter_list|)
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
name|relation
range|:
name|relationFilters
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
name|entityRelations
operator|.
name|get
argument_list|(
name|relation
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
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|id
range|:
name|relation
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|ids
operator|.
name|contains
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    *    * @param map the map of key/value pairs in an entity    * @param filters the map of key/value pairs for filtering    * @return a boolean flag to indicate if both match    */
DECL|method|matchFilters (Map<String, ? extends Object> map, Map<String, ? extends Object> filters)
specifier|public
specifier|static
name|boolean
name|matchFilters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|Object
argument_list|>
name|map
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|Object
argument_list|>
name|filters
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|Object
argument_list|>
name|filter
range|:
name|filters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|map
operator|.
name|get
argument_list|(
name|filter
operator|.
name|getKey
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
return|return
literal|false
return|;
block|}
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
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    *    * @param entityEvents the set of event objects in an entity    * @param eventFilters the set of event Ids for filtering    * @return a boolean flag to indicate if both match    */
DECL|method|matchEventFilters (Set<TimelineEvent> entityEvents, Set<String> eventFilters)
specifier|public
specifier|static
name|boolean
name|matchEventFilters
parameter_list|(
name|Set
argument_list|<
name|TimelineEvent
argument_list|>
name|entityEvents
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|eventFilters
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|eventIds
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineEvent
name|event
range|:
name|entityEvents
control|)
block|{
name|eventIds
operator|.
name|add
argument_list|(
name|event
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|eventFilter
range|:
name|eventFilters
control|)
block|{
if|if
condition|(
operator|!
name|eventIds
operator|.
name|contains
argument_list|(
name|eventFilter
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**    *    * @param metrics the set of metric objects in an entity    * @param metricFilters the set of metric Ids for filtering    * @return a boolean flag to indicate if both match    */
DECL|method|matchMetricFilters (Set<TimelineMetric> metrics, Set<String> metricFilters)
specifier|public
specifier|static
name|boolean
name|matchMetricFilters
parameter_list|(
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|metricFilters
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|metricIds
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineMetric
name|metric
range|:
name|metrics
control|)
block|{
name|metricIds
operator|.
name|add
argument_list|(
name|metric
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|metricFilter
range|:
name|metricFilters
control|)
block|{
if|if
condition|(
operator|!
name|metricIds
operator|.
name|contains
argument_list|(
name|metricFilter
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

