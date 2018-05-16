begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.collector
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
name|collector
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|conf
operator|.
name|Configuration
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
name|security
operator|.
name|UserGroupInformation
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
name|CompositeService
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
name|timelineservice
operator|.
name|TimelineMetricOperation
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
name|TimelineWriteResponse
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
name|TimelineWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Service that handles writes to the timeline service and writes them to the  * backing storage.  *  * Classes that extend this can add their own lifecycle management or  * customization of request handling.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|TimelineCollector
specifier|public
specifier|abstract
class|class
name|TimelineCollector
extends|extends
name|CompositeService
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TimelineCollector
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SEPARATOR
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|"_"
decl_stmt|;
DECL|field|writer
specifier|private
name|TimelineWriter
name|writer
decl_stmt|;
DECL|field|aggregationGroups
specifier|private
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|aggregationGroups
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|entityTypesSkipAggregation
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|entityTypesSkipAggregation
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|readyToAggregate
specifier|private
specifier|volatile
name|boolean
name|readyToAggregate
init|=
literal|false
decl_stmt|;
DECL|field|isStopped
specifier|private
specifier|volatile
name|boolean
name|isStopped
init|=
literal|false
decl_stmt|;
DECL|method|TimelineCollector (String name)
specifier|public
name|TimelineCollector
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|isStopped
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|isStopped ()
name|boolean
name|isStopped
parameter_list|()
block|{
return|return
name|isStopped
return|;
block|}
DECL|method|setWriter (TimelineWriter w)
specifier|protected
name|void
name|setWriter
parameter_list|(
name|TimelineWriter
name|w
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|w
expr_stmt|;
block|}
DECL|method|getAggregationGroups ()
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|getAggregationGroups
parameter_list|()
block|{
return|return
name|aggregationGroups
return|;
block|}
DECL|method|setReadyToAggregate ()
specifier|protected
name|void
name|setReadyToAggregate
parameter_list|()
block|{
name|readyToAggregate
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|isReadyToAggregate ()
specifier|protected
name|boolean
name|isReadyToAggregate
parameter_list|()
block|{
return|return
name|readyToAggregate
return|;
block|}
comment|/**    * Method to decide the set of timeline entity types the collector should    * skip on aggregations. Subclasses may want to override this method to    * customize their own behaviors.    *    * @return A set of strings consists of all types the collector should skip.    */
DECL|method|getEntityTypesSkipAggregation ()
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getEntityTypesSkipAggregation
parameter_list|()
block|{
return|return
name|entityTypesSkipAggregation
return|;
block|}
DECL|method|getTimelineEntityContext ()
specifier|public
specifier|abstract
name|TimelineCollectorContext
name|getTimelineEntityContext
parameter_list|()
function_decl|;
comment|/**    * Handles entity writes. These writes are synchronous and are written to the    * backing storage without buffering/batching. If any entity already exists,    * it results in an update of the entity.    *    * This method should be reserved for selected critical entities and events.    * For normal voluminous writes one should use the async method    * {@link #putEntitiesAsync(TimelineEntities, UserGroupInformation)}.    *    * @param entities entities to post    * @param callerUgi the caller UGI    * @return the response that contains the result of the post.    * @throws IOException if there is any exception encountered while putting    *     entities.    */
DECL|method|putEntities (TimelineEntities entities, UserGroupInformation callerUgi)
specifier|public
name|TimelineWriteResponse
name|putEntities
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"putEntities(entities="
operator|+
name|entities
operator|+
literal|", callerUgi="
operator|+
name|callerUgi
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|TimelineWriteResponse
name|response
decl_stmt|;
comment|// synchronize on the writer object so that no other threads can
comment|// flush the writer buffer concurrently and swallow any exception
comment|// caused by the timeline enitites that are being put here.
synchronized|synchronized
init|(
name|writer
init|)
block|{
name|response
operator|=
name|writeTimelineEntities
argument_list|(
name|entities
argument_list|,
name|callerUgi
argument_list|)
expr_stmt|;
name|flushBufferedTimelineEntities
argument_list|()
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
comment|/**    * Add or update an domain. If the domain already exists, only the owner    * and the admin can update it.    *    * @param domain    domain to post    * @param callerUgi the caller UGI    * @return the response that contains the result of the post.    * @throws IOException if there is any exception encountered while putting    *                     domain.    */
DECL|method|putDomain (TimelineDomain domain, UserGroupInformation callerUgi)
specifier|public
name|TimelineWriteResponse
name|putDomain
parameter_list|(
name|TimelineDomain
name|domain
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"putDomain(domain="
operator|+
name|domain
operator|+
literal|", callerUgi="
operator|+
name|callerUgi
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|TimelineWriteResponse
name|response
decl_stmt|;
synchronized|synchronized
init|(
name|writer
init|)
block|{
specifier|final
name|TimelineCollectorContext
name|context
init|=
name|getTimelineEntityContext
argument_list|()
decl_stmt|;
name|response
operator|=
name|writer
operator|.
name|write
argument_list|(
name|context
argument_list|,
name|domain
argument_list|)
expr_stmt|;
name|flushBufferedTimelineEntities
argument_list|()
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
DECL|method|writeTimelineEntities ( TimelineEntities entities, UserGroupInformation callerUgi)
specifier|private
name|TimelineWriteResponse
name|writeTimelineEntities
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Update application metrics for aggregation
name|updateAggregateStatus
argument_list|(
name|entities
argument_list|,
name|aggregationGroups
argument_list|,
name|getEntityTypesSkipAggregation
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|TimelineCollectorContext
name|context
init|=
name|getTimelineEntityContext
argument_list|()
decl_stmt|;
return|return
name|writer
operator|.
name|write
argument_list|(
name|context
argument_list|,
name|entities
argument_list|,
name|callerUgi
argument_list|)
return|;
block|}
comment|/**    * Flush buffered timeline entities, if any.    * @throws IOException if there is any exception encountered while    *      flushing buffered entities.    */
DECL|method|flushBufferedTimelineEntities ()
specifier|private
name|void
name|flushBufferedTimelineEntities
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Handles entity writes in an asynchronous manner. The method returns as soon    * as validation is done. No promises are made on how quickly it will be    * written to the backing storage or if it will always be written to the    * backing storage. Multiple writes to the same entities may be batched and    * appropriate values updated and result in fewer writes to the backing    * storage.    *    * @param entities entities to post    * @param callerUgi the caller UGI    * @throws IOException if there is any exception encounted while putting    *     entities.    */
DECL|method|putEntitiesAsync (TimelineEntities entities, UserGroupInformation callerUgi)
specifier|public
name|void
name|putEntitiesAsync
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|,
name|UserGroupInformation
name|callerUgi
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"putEntitiesAsync(entities="
operator|+
name|entities
operator|+
literal|", callerUgi="
operator|+
name|callerUgi
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
name|writeTimelineEntities
argument_list|(
name|entities
argument_list|,
name|callerUgi
argument_list|)
expr_stmt|;
block|}
comment|/**    * Aggregate all metrics in given timeline entities with no predefined states.    *    * @param entities Entities to aggregate    * @param resultEntityId Id of the result entity    * @param resultEntityType Type of the result entity    * @param needsGroupIdInResult Marks if we want the aggregation group id in    *                             each aggregated metrics.    * @return A timeline entity that contains all aggregated TimelineMetric.    */
DECL|method|aggregateEntities ( TimelineEntities entities, String resultEntityId, String resultEntityType, boolean needsGroupIdInResult)
specifier|public
specifier|static
name|TimelineEntity
name|aggregateEntities
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|,
name|String
name|resultEntityId
parameter_list|,
name|String
name|resultEntityType
parameter_list|,
name|boolean
name|needsGroupIdInResult
parameter_list|)
block|{
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|aggregationGroups
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|updateAggregateStatus
argument_list|(
name|entities
argument_list|,
name|aggregationGroups
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|needsGroupIdInResult
condition|)
block|{
return|return
name|aggregate
argument_list|(
name|aggregationGroups
argument_list|,
name|resultEntityId
argument_list|,
name|resultEntityType
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|aggregateWithoutGroupId
argument_list|(
name|aggregationGroups
argument_list|,
name|resultEntityId
argument_list|,
name|resultEntityType
argument_list|)
return|;
block|}
block|}
comment|/**    * Update the aggregation status table for a timeline collector.    *    * @param entities Entities to update    * @param aggregationGroups Aggregation status table    * @param typesToSkip Entity types that we can safely assume to skip updating    */
DECL|method|updateAggregateStatus ( TimelineEntities entities, ConcurrentMap<String, AggregationStatusTable> aggregationGroups, Set<String> typesToSkip)
specifier|static
name|void
name|updateAggregateStatus
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|,
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|aggregationGroups
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|typesToSkip
parameter_list|)
block|{
for|for
control|(
name|TimelineEntity
name|e
range|:
name|entities
operator|.
name|getEntities
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|typesToSkip
operator|!=
literal|null
operator|&&
name|typesToSkip
operator|.
name|contains
argument_list|(
name|e
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|||
name|e
operator|.
name|getMetrics
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|AggregationStatusTable
name|aggrTable
init|=
name|aggregationGroups
operator|.
name|get
argument_list|(
name|e
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggrTable
operator|==
literal|null
condition|)
block|{
name|AggregationStatusTable
name|table
init|=
operator|new
name|AggregationStatusTable
argument_list|()
decl_stmt|;
name|aggrTable
operator|=
name|aggregationGroups
operator|.
name|putIfAbsent
argument_list|(
name|e
operator|.
name|getType
argument_list|()
argument_list|,
name|table
argument_list|)
expr_stmt|;
if|if
condition|(
name|aggrTable
operator|==
literal|null
condition|)
block|{
name|aggrTable
operator|=
name|table
expr_stmt|;
block|}
block|}
name|aggrTable
operator|.
name|update
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Aggregate internal status and generate timeline entities for the    * aggregation results.    *    * @param aggregationGroups Aggregation status table    * @param resultEntityId Id of the result entity    * @param resultEntityType Type of the result entity    * @return A timeline entity that contains all aggregated TimelineMetric.    */
DECL|method|aggregate ( Map<String, AggregationStatusTable> aggregationGroups, String resultEntityId, String resultEntityType)
specifier|static
name|TimelineEntity
name|aggregate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|aggregationGroups
parameter_list|,
name|String
name|resultEntityId
parameter_list|,
name|String
name|resultEntityType
parameter_list|)
block|{
name|TimelineEntity
name|result
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|result
operator|.
name|setId
argument_list|(
name|resultEntityId
argument_list|)
expr_stmt|;
name|result
operator|.
name|setType
argument_list|(
name|resultEntityType
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|entry
range|:
name|aggregationGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|aggregateAllTo
argument_list|(
name|result
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Aggregate internal status and generate timeline entities for the    * aggregation results. The result metrics will not have aggregation group    * information.    *    * @param aggregationGroups Aggregation status table    * @param resultEntityId Id of the result entity    * @param resultEntityType Type of the result entity    * @return A timeline entity that contains all aggregated TimelineMetric.    */
DECL|method|aggregateWithoutGroupId ( Map<String, AggregationStatusTable> aggregationGroups, String resultEntityId, String resultEntityType)
specifier|static
name|TimelineEntity
name|aggregateWithoutGroupId
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|aggregationGroups
parameter_list|,
name|String
name|resultEntityId
parameter_list|,
name|String
name|resultEntityType
parameter_list|)
block|{
name|TimelineEntity
name|result
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|result
operator|.
name|setId
argument_list|(
name|resultEntityId
argument_list|)
expr_stmt|;
name|result
operator|.
name|setType
argument_list|(
name|resultEntityType
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|entry
range|:
name|aggregationGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|aggregateAllTo
argument_list|(
name|result
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|// Note: In memory aggregation is performed in an eventually consistent
comment|// fashion.
DECL|class|AggregationStatusTable
specifier|protected
specifier|static
class|class
name|AggregationStatusTable
block|{
comment|// On aggregation, for each metric, aggregate all per-entity accumulated
comment|// metrics. We only use the id and type for TimelineMetrics in the key set
comment|// of this table.
specifier|private
name|ConcurrentMap
argument_list|<
name|TimelineMetric
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|TimelineMetric
argument_list|>
argument_list|>
DECL|field|aggregateTable
name|aggregateTable
decl_stmt|;
DECL|method|AggregationStatusTable ()
specifier|public
name|AggregationStatusTable
parameter_list|()
block|{
name|aggregateTable
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|update (TimelineEntity incoming)
specifier|public
name|void
name|update
parameter_list|(
name|TimelineEntity
name|incoming
parameter_list|)
block|{
name|String
name|entityId
init|=
name|incoming
operator|.
name|getId
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineMetric
name|m
range|:
name|incoming
operator|.
name|getMetrics
argument_list|()
control|)
block|{
comment|// Skip if the metric does not need aggregation
if|if
condition|(
name|m
operator|.
name|getRealtimeAggregationOp
argument_list|()
operator|==
name|TimelineMetricOperation
operator|.
name|NOP
condition|)
block|{
continue|continue;
block|}
comment|// Update aggregateTable
name|Map
argument_list|<
name|String
argument_list|,
name|TimelineMetric
argument_list|>
name|aggrRow
init|=
name|aggregateTable
operator|.
name|get
argument_list|(
name|m
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggrRow
operator|==
literal|null
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|TimelineMetric
argument_list|>
name|tempRow
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|aggrRow
operator|=
name|aggregateTable
operator|.
name|putIfAbsent
argument_list|(
name|m
argument_list|,
name|tempRow
argument_list|)
expr_stmt|;
if|if
condition|(
name|aggrRow
operator|==
literal|null
condition|)
block|{
name|aggrRow
operator|=
name|tempRow
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|aggrRow
init|)
block|{
name|aggrRow
operator|.
name|put
argument_list|(
name|entityId
argument_list|,
name|m
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|aggregateTo (TimelineMetric metric, TimelineEntity e, String aggregationGroupId)
specifier|public
name|TimelineEntity
name|aggregateTo
parameter_list|(
name|TimelineMetric
name|metric
parameter_list|,
name|TimelineEntity
name|e
parameter_list|,
name|String
name|aggregationGroupId
parameter_list|)
block|{
if|if
condition|(
name|metric
operator|.
name|getRealtimeAggregationOp
argument_list|()
operator|==
name|TimelineMetricOperation
operator|.
name|NOP
condition|)
block|{
return|return
name|e
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|TimelineMetric
argument_list|>
name|aggrRow
init|=
name|aggregateTable
operator|.
name|get
argument_list|(
name|metric
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggrRow
operator|!=
literal|null
condition|)
block|{
name|TimelineMetric
name|aggrMetric
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
if|if
condition|(
name|aggregationGroupId
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|aggrMetric
operator|.
name|setId
argument_list|(
name|metric
operator|.
name|getId
argument_list|()
operator|+
name|SEPARATOR
operator|+
name|aggregationGroupId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|aggrMetric
operator|.
name|setId
argument_list|(
name|metric
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|aggrMetric
operator|.
name|setRealtimeAggregationOp
argument_list|(
name|TimelineMetricOperation
operator|.
name|NOP
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|status
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|aggrRow
init|)
block|{
for|for
control|(
name|TimelineMetric
name|m
range|:
name|aggrRow
operator|.
name|values
argument_list|()
control|)
block|{
name|TimelineMetric
operator|.
name|aggregateTo
argument_list|(
name|m
argument_list|,
name|aggrMetric
argument_list|,
name|status
argument_list|)
expr_stmt|;
comment|// getRealtimeAggregationOp returns an enum so we can directly
comment|// compare with "!=".
if|if
condition|(
name|m
operator|.
name|getRealtimeAggregationOp
argument_list|()
operator|!=
name|aggrMetric
operator|.
name|getRealtimeAggregationOp
argument_list|()
condition|)
block|{
name|aggrMetric
operator|.
name|setRealtimeAggregationOp
argument_list|(
name|m
operator|.
name|getRealtimeAggregationOp
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|aggrRow
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
init|=
name|e
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|remove
argument_list|(
name|aggrMetric
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|aggrMetric
argument_list|)
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
DECL|method|aggregateAllTo (TimelineEntity e, String aggregationGroupId)
specifier|public
name|TimelineEntity
name|aggregateAllTo
parameter_list|(
name|TimelineEntity
name|e
parameter_list|,
name|String
name|aggregationGroupId
parameter_list|)
block|{
for|for
control|(
name|TimelineMetric
name|m
range|:
name|aggregateTable
operator|.
name|keySet
argument_list|()
control|)
block|{
name|aggregateTo
argument_list|(
name|m
argument_list|,
name|e
argument_list|,
name|aggregationGroupId
argument_list|)
expr_stmt|;
block|}
return|return
name|e
return|;
block|}
block|}
block|}
end_class

end_unit

