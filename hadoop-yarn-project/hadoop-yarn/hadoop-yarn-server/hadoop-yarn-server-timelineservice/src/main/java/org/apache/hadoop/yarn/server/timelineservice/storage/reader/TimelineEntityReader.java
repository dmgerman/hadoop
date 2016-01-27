begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.reader
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
name|reader
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
name|NavigableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableSet
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
name|TreeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|hbase
operator|.
name|client
operator|.
name|Connection
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
name|hbase
operator|.
name|client
operator|.
name|Result
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
name|hbase
operator|.
name|client
operator|.
name|ResultScanner
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
name|hbase
operator|.
name|filter
operator|.
name|FilterList
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
name|storage
operator|.
name|TimelineReader
operator|.
name|Field
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
name|common
operator|.
name|BaseTable
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
name|common
operator|.
name|ColumnPrefix
import|;
end_import

begin_comment
comment|/**  * The base class for reading and deserializing timeline entities from the  * HBase storage. Different types can be defined for different types of the  * entities that are being requested.  */
end_comment

begin_class
DECL|class|TimelineEntityReader
specifier|public
specifier|abstract
class|class
name|TimelineEntityReader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TimelineEntityReader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_BEGIN_TIME
specifier|protected
specifier|static
specifier|final
name|long
name|DEFAULT_BEGIN_TIME
init|=
literal|0L
decl_stmt|;
DECL|field|DEFAULT_END_TIME
specifier|protected
specifier|static
specifier|final
name|long
name|DEFAULT_END_TIME
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
DECL|field|singleEntityRead
specifier|protected
specifier|final
name|boolean
name|singleEntityRead
decl_stmt|;
DECL|field|userId
specifier|protected
name|String
name|userId
decl_stmt|;
DECL|field|clusterId
specifier|protected
name|String
name|clusterId
decl_stmt|;
DECL|field|flowName
specifier|protected
name|String
name|flowName
decl_stmt|;
DECL|field|flowRunId
specifier|protected
name|Long
name|flowRunId
decl_stmt|;
DECL|field|appId
specifier|protected
name|String
name|appId
decl_stmt|;
DECL|field|entityType
specifier|protected
name|String
name|entityType
decl_stmt|;
DECL|field|fieldsToRetrieve
specifier|protected
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
decl_stmt|;
comment|// used only for a single entity read mode
DECL|field|entityId
specifier|protected
name|String
name|entityId
decl_stmt|;
comment|// used only for multiple entity read mode
DECL|field|limit
specifier|protected
name|Long
name|limit
decl_stmt|;
DECL|field|createdTimeBegin
specifier|protected
name|Long
name|createdTimeBegin
decl_stmt|;
DECL|field|createdTimeEnd
specifier|protected
name|Long
name|createdTimeEnd
decl_stmt|;
DECL|field|relatesTo
specifier|protected
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
decl_stmt|;
DECL|field|isRelatedTo
specifier|protected
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
decl_stmt|;
DECL|field|infoFilters
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|infoFilters
decl_stmt|;
DECL|field|configFilters
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|configFilters
decl_stmt|;
DECL|field|metricFilters
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|metricFilters
decl_stmt|;
DECL|field|eventFilters
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|eventFilters
decl_stmt|;
DECL|field|confsToRetrieve
specifier|protected
name|TimelineFilterList
name|confsToRetrieve
decl_stmt|;
DECL|field|metricsToRetrieve
specifier|protected
name|TimelineFilterList
name|metricsToRetrieve
decl_stmt|;
comment|/**    * Main table the entity reader uses.    */
DECL|field|table
specifier|protected
name|BaseTable
argument_list|<
name|?
argument_list|>
name|table
decl_stmt|;
comment|/**    * Specifies whether keys for this table are sorted in a manner where entities    * can be retrieved by created time. If true, it will be sufficient to collect    * the first results as specified by the limit. Otherwise all matched entities    * will be fetched and then limit applied.    */
DECL|field|sortedKeys
specifier|private
name|boolean
name|sortedKeys
init|=
literal|false
decl_stmt|;
comment|/**    * Instantiates a reader for multiple-entity reads.    */
DECL|method|TimelineEntityReader (String userId, String clusterId, String flowName, Long flowRunId, String appId, String entityType, Long limit, Long createdTimeBegin, Long createdTimeEnd, Map<String, Set<String>> relatesTo, Map<String, Set<String>> isRelatedTo, Map<String, Object> infoFilters, Map<String, String> configFilters, Set<String> metricFilters, Set<String> eventFilters, TimelineFilterList confsToRetrieve, TimelineFilterList metricsToRetrieve, EnumSet<Field> fieldsToRetrieve, boolean sortedKeys)
specifier|protected
name|TimelineEntityReader
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
parameter_list|,
name|boolean
name|sortedKeys
parameter_list|)
block|{
name|this
operator|.
name|singleEntityRead
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|sortedKeys
operator|=
name|sortedKeys
expr_stmt|;
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|flowName
operator|=
name|flowName
expr_stmt|;
name|this
operator|.
name|flowRunId
operator|=
name|flowRunId
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|entityType
operator|=
name|entityType
expr_stmt|;
name|this
operator|.
name|fieldsToRetrieve
operator|=
name|fieldsToRetrieve
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
name|this
operator|.
name|createdTimeBegin
operator|=
name|createdTimeBegin
expr_stmt|;
name|this
operator|.
name|createdTimeEnd
operator|=
name|createdTimeEnd
expr_stmt|;
name|this
operator|.
name|relatesTo
operator|=
name|relatesTo
expr_stmt|;
name|this
operator|.
name|isRelatedTo
operator|=
name|isRelatedTo
expr_stmt|;
name|this
operator|.
name|infoFilters
operator|=
name|infoFilters
expr_stmt|;
name|this
operator|.
name|configFilters
operator|=
name|configFilters
expr_stmt|;
name|this
operator|.
name|metricFilters
operator|=
name|metricFilters
expr_stmt|;
name|this
operator|.
name|eventFilters
operator|=
name|eventFilters
expr_stmt|;
name|this
operator|.
name|confsToRetrieve
operator|=
name|confsToRetrieve
expr_stmt|;
name|this
operator|.
name|metricsToRetrieve
operator|=
name|metricsToRetrieve
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|getTable
argument_list|()
expr_stmt|;
block|}
comment|/**    * Instantiates a reader for single-entity reads.    */
DECL|method|TimelineEntityReader (String userId, String clusterId, String flowName, Long flowRunId, String appId, String entityType, String entityId, TimelineFilterList confsToRetrieve, TimelineFilterList metricsToRetrieve, EnumSet<Field> fieldsToRetrieve)
specifier|protected
name|TimelineEntityReader
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
block|{
name|this
operator|.
name|singleEntityRead
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|userId
operator|=
name|userId
expr_stmt|;
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|flowName
operator|=
name|flowName
expr_stmt|;
name|this
operator|.
name|flowRunId
operator|=
name|flowRunId
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
name|this
operator|.
name|entityType
operator|=
name|entityType
expr_stmt|;
name|this
operator|.
name|fieldsToRetrieve
operator|=
name|fieldsToRetrieve
expr_stmt|;
name|this
operator|.
name|entityId
operator|=
name|entityId
expr_stmt|;
name|this
operator|.
name|confsToRetrieve
operator|=
name|confsToRetrieve
expr_stmt|;
name|this
operator|.
name|metricsToRetrieve
operator|=
name|metricsToRetrieve
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|getTable
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a {@link FilterList} based on fields, confs and metrics to    * retrieve. This filter list will be set in Scan/Get objects to trim down    * results fetched from HBase back-end storage.    * @return a {@link FilterList} object.    */
DECL|method|constructFilterListBasedOnFields ()
specifier|protected
specifier|abstract
name|FilterList
name|constructFilterListBasedOnFields
parameter_list|()
function_decl|;
comment|/**    * Reads and deserializes a single timeline entity from the HBase storage.    */
DECL|method|readEntity (Configuration hbaseConf, Connection conn)
specifier|public
name|TimelineEntity
name|readEntity
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|validateParams
argument_list|()
expr_stmt|;
name|augmentParams
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
expr_stmt|;
name|FilterList
name|filterList
init|=
name|constructFilterListBasedOnFields
argument_list|()
decl_stmt|;
name|Result
name|result
init|=
name|getResult
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|,
name|filterList
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Could not find a matching row.
name|LOG
operator|.
name|info
argument_list|(
literal|"Cannot find matching entity of type "
operator|+
name|entityType
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|parseEntity
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * Reads and deserializes a set of timeline entities from the HBase storage.    * It goes through all the results available, and returns the number of    * entries as specified in the limit in the entity's natural sort order.    */
DECL|method|readEntities (Configuration hbaseConf, Connection conn)
specifier|public
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|readEntities
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
name|validateParams
argument_list|()
expr_stmt|;
name|augmentParams
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
expr_stmt|;
name|NavigableSet
argument_list|<
name|TimelineEntity
argument_list|>
name|entities
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|FilterList
name|filterList
init|=
name|constructFilterListBasedOnFields
argument_list|()
decl_stmt|;
name|ResultScanner
name|results
init|=
name|getResults
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|,
name|filterList
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|Result
name|result
range|:
name|results
control|)
block|{
name|TimelineEntity
name|entity
init|=
name|parseEntity
argument_list|(
name|result
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
name|entities
operator|.
name|add
argument_list|(
name|entity
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|sortedKeys
condition|)
block|{
if|if
condition|(
name|entities
operator|.
name|size
argument_list|()
operator|>
name|limit
condition|)
block|{
name|entities
operator|.
name|pollLast
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|entities
operator|.
name|size
argument_list|()
operator|==
name|limit
condition|)
block|{
break|break;
block|}
block|}
block|}
return|return
name|entities
return|;
block|}
finally|finally
block|{
name|results
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the main table to be used by the entity reader.    */
DECL|method|getTable ()
specifier|protected
specifier|abstract
name|BaseTable
argument_list|<
name|?
argument_list|>
name|getTable
parameter_list|()
function_decl|;
comment|/**    * Validates the required parameters to read the entities.    */
DECL|method|validateParams ()
specifier|protected
specifier|abstract
name|void
name|validateParams
parameter_list|()
function_decl|;
comment|/**    * Sets certain parameters to defaults if the values are not provided.    */
DECL|method|augmentParams (Configuration hbaseConf, Connection conn)
specifier|protected
specifier|abstract
name|void
name|augmentParams
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Fetches a {@link Result} instance for a single-entity read.    *    * @return the {@link Result} instance or null if no such record is found.    */
DECL|method|getResult (Configuration hbaseConf, Connection conn, FilterList filterList)
specifier|protected
specifier|abstract
name|Result
name|getResult
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|,
name|FilterList
name|filterList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Fetches a {@link ResultScanner} for a multi-entity read.    */
DECL|method|getResults (Configuration hbaseConf, Connection conn, FilterList filterList)
specifier|protected
specifier|abstract
name|ResultScanner
name|getResults
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|,
name|FilterList
name|filterList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Given a {@link Result} instance, deserializes and creates a    * {@link TimelineEntity}.    *    * @return the {@link TimelineEntity} instance, or null if the {@link Result}    * is null or empty.    */
DECL|method|parseEntity (Result result)
specifier|protected
specifier|abstract
name|TimelineEntity
name|parseEntity
parameter_list|(
name|Result
name|result
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Helper method for reading and deserializing {@link TimelineMetric} objects    * using the specified column prefix. The timeline metrics then are added to    * the given timeline entity.    */
DECL|method|readMetrics (TimelineEntity entity, Result result, ColumnPrefix<?> columnPrefix)
specifier|protected
name|void
name|readMetrics
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|,
name|Result
name|result
parameter_list|,
name|ColumnPrefix
argument_list|<
name|?
argument_list|>
name|columnPrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Number
argument_list|>
argument_list|>
name|metricsResult
init|=
name|columnPrefix
operator|.
name|readResultsWithTimestamps
argument_list|(
name|result
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|Number
argument_list|>
argument_list|>
name|metricResult
range|:
name|metricsResult
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|TimelineMetric
name|metric
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|metric
operator|.
name|setId
argument_list|(
name|metricResult
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
comment|// Simply assume that if the value set contains more than 1 elements, the
comment|// metric is a TIME_SERIES metric, otherwise, it's a SINGLE_VALUE metric
name|metric
operator|.
name|setType
argument_list|(
name|metricResult
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|?
name|TimelineMetric
operator|.
name|Type
operator|.
name|TIME_SERIES
else|:
name|TimelineMetric
operator|.
name|Type
operator|.
name|SINGLE_VALUE
argument_list|)
expr_stmt|;
name|metric
operator|.
name|addValues
argument_list|(
name|metricResult
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addMetric
argument_list|(
name|metric
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

