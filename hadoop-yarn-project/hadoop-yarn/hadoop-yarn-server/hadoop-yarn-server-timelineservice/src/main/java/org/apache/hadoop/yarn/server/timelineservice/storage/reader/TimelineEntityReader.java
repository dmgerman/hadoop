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
name|TimelineDataToRetrieve
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
name|TimelineEntityFilters
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
name|TimelineReaderContext
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
name|StringKeyConverter
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
DECL|field|singleEntityRead
specifier|private
specifier|final
name|boolean
name|singleEntityRead
decl_stmt|;
DECL|field|context
specifier|private
name|TimelineReaderContext
name|context
decl_stmt|;
DECL|field|dataToRetrieve
specifier|private
name|TimelineDataToRetrieve
name|dataToRetrieve
decl_stmt|;
comment|// used only for multiple entity read mode
DECL|field|filters
specifier|private
name|TimelineEntityFilters
name|filters
decl_stmt|;
comment|/**    * Main table the entity reader uses.    */
DECL|field|table
specifier|private
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
comment|/**    * Instantiates a reader for multiple-entity reads.    *    * @param ctxt Reader context which defines the scope in which query has to be    *     made.    * @param entityFilters Filters which limit the entities returned.    * @param toRetrieve Data to retrieve for each entity.    * @param sortedKeys Specifies whether key for this table are sorted or not.    *     If sorted, entities can be retrieved by created time.    */
DECL|method|TimelineEntityReader (TimelineReaderContext ctxt, TimelineEntityFilters entityFilters, TimelineDataToRetrieve toRetrieve, boolean sortedKeys)
specifier|protected
name|TimelineEntityReader
parameter_list|(
name|TimelineReaderContext
name|ctxt
parameter_list|,
name|TimelineEntityFilters
name|entityFilters
parameter_list|,
name|TimelineDataToRetrieve
name|toRetrieve
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
name|context
operator|=
name|ctxt
expr_stmt|;
name|this
operator|.
name|dataToRetrieve
operator|=
name|toRetrieve
expr_stmt|;
name|this
operator|.
name|filters
operator|=
name|entityFilters
expr_stmt|;
name|this
operator|.
name|setTable
argument_list|(
name|getTable
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Instantiates a reader for single-entity reads.    *    * @param ctxt Reader context which defines the scope in which query has to be    *     made.    * @param toRetrieve Data to retrieve for each entity.    */
DECL|method|TimelineEntityReader (TimelineReaderContext ctxt, TimelineDataToRetrieve toRetrieve)
specifier|protected
name|TimelineEntityReader
parameter_list|(
name|TimelineReaderContext
name|ctxt
parameter_list|,
name|TimelineDataToRetrieve
name|toRetrieve
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
name|context
operator|=
name|ctxt
expr_stmt|;
name|this
operator|.
name|dataToRetrieve
operator|=
name|toRetrieve
expr_stmt|;
name|this
operator|.
name|setTable
argument_list|(
name|getTable
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a {@link FilterList} based on fields, confs and metrics to    * retrieve. This filter list will be set in Scan/Get objects to trim down    * results fetched from HBase back-end storage. This is called only for    * multiple entity reads.    *    * @return a {@link FilterList} object.    * @throws IOException if any problem occurs while creating filter list.    */
DECL|method|constructFilterListBasedOnFields ()
specifier|protected
specifier|abstract
name|FilterList
name|constructFilterListBasedOnFields
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a {@link FilterList} based on info, config and metric filters. This    * filter list will be set in HBase Get to trim down results fetched from    * HBase back-end storage.    *    * @return a {@link FilterList} object.    * @throws IOException if any problem occurs while creating filter list.    */
DECL|method|constructFilterListBasedOnFilters ()
specifier|protected
specifier|abstract
name|FilterList
name|constructFilterListBasedOnFilters
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Combines filter lists created based on fields and based on filters.    *    * @return a {@link FilterList} object if it can be constructed. Returns null,    * if filter list cannot be created either on the basis of filters or on the    * basis of fields.    * @throws IOException if any problem occurs while creating filter list.    */
DECL|method|createFilterList ()
specifier|private
name|FilterList
name|createFilterList
parameter_list|()
throws|throws
name|IOException
block|{
name|FilterList
name|listBasedOnFilters
init|=
name|constructFilterListBasedOnFilters
argument_list|()
decl_stmt|;
name|boolean
name|hasListBasedOnFilters
init|=
name|listBasedOnFilters
operator|!=
literal|null
operator|&&
operator|!
name|listBasedOnFilters
operator|.
name|getFilters
argument_list|()
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
name|FilterList
name|listBasedOnFields
init|=
name|constructFilterListBasedOnFields
argument_list|()
decl_stmt|;
name|boolean
name|hasListBasedOnFields
init|=
name|listBasedOnFields
operator|!=
literal|null
operator|&&
operator|!
name|listBasedOnFields
operator|.
name|getFilters
argument_list|()
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
comment|// If filter lists based on both filters and fields can be created,
comment|// combine them in a new filter list and return it.
comment|// If either one of them has been created, return that filter list.
comment|// Return null, if none of the filter lists can be created. This indicates
comment|// that no filter list needs to be added to HBase Scan as filters are not
comment|// specified for the query or only the default view of entity needs to be
comment|// returned.
if|if
condition|(
name|hasListBasedOnFilters
operator|&&
name|hasListBasedOnFields
condition|)
block|{
name|FilterList
name|list
init|=
operator|new
name|FilterList
argument_list|()
decl_stmt|;
name|list
operator|.
name|addFilter
argument_list|(
name|listBasedOnFilters
argument_list|)
expr_stmt|;
name|list
operator|.
name|addFilter
argument_list|(
name|listBasedOnFields
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
elseif|else
if|if
condition|(
name|hasListBasedOnFilters
condition|)
block|{
return|return
name|listBasedOnFilters
return|;
block|}
elseif|else
if|if
condition|(
name|hasListBasedOnFields
condition|)
block|{
return|return
name|listBasedOnFields
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getContext ()
specifier|protected
name|TimelineReaderContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
DECL|method|getDataToRetrieve ()
specifier|protected
name|TimelineDataToRetrieve
name|getDataToRetrieve
parameter_list|()
block|{
return|return
name|dataToRetrieve
return|;
block|}
DECL|method|getFilters ()
specifier|protected
name|TimelineEntityFilters
name|getFilters
parameter_list|()
block|{
return|return
name|filters
return|;
block|}
comment|/**    * Create a {@link TimelineEntityFilters} object with default values for    * filters.    */
DECL|method|createFiltersIfNull ()
specifier|protected
name|void
name|createFiltersIfNull
parameter_list|()
block|{
if|if
condition|(
name|filters
operator|==
literal|null
condition|)
block|{
name|filters
operator|=
operator|new
name|TimelineEntityFilters
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Reads and deserializes a single timeline entity from the HBase storage.    *    * @param hbaseConf HBase Configuration.    * @param conn HBase Connection.    * @return A<cite>TimelineEntity</cite> object.    * @throws IOException if there is any exception encountered while reading    *     entity.    */
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
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
name|filterList
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"FilterList created for get is - "
operator|+
name|filterList
argument_list|)
expr_stmt|;
block|}
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
name|context
operator|.
name|getEntityType
argument_list|()
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
comment|/**    * Reads and deserializes a set of timeline entities from the HBase storage.    * It goes through all the results available, and returns the number of    * entries as specified in the limit in the entity's natural sort order.    *    * @param hbaseConf HBase Configuration.    * @param conn HBase Connection.    * @return a set of<cite>TimelineEntity</cite> objects.    * @throws IOException if any exception is encountered while reading entities.    */
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
name|createFilterList
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
name|filterList
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"FilterList created for scan is - "
operator|+
name|filterList
argument_list|)
expr_stmt|;
block|}
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
name|filters
operator|.
name|getLimit
argument_list|()
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
name|filters
operator|.
name|getLimit
argument_list|()
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
comment|/**    * Returns the main table to be used by the entity reader.    *    * @return A reference to the table.    */
DECL|method|getTable ()
specifier|protected
name|BaseTable
argument_list|<
name|?
argument_list|>
name|getTable
parameter_list|()
block|{
return|return
name|table
return|;
block|}
comment|/**    * Validates the required parameters to read the entities.    */
DECL|method|validateParams ()
specifier|protected
specifier|abstract
name|void
name|validateParams
parameter_list|()
function_decl|;
comment|/**    * Sets certain parameters to defaults if the values are not provided.    *    * @param hbaseConf HBase Configuration.    * @param conn HBase Connection.    * @throws IOException if any exception is encountered while setting params.    */
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
comment|/**    * Fetches a {@link Result} instance for a single-entity read.    *    * @param hbaseConf HBase Configuration.    * @param conn HBase Connection.    * @param filterList filter list which will be applied to HBase Get.    * @return the {@link Result} instance or null if no such record is found.    * @throws IOException if any exception is encountered while getting result.    */
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
comment|/**    * Fetches a {@link ResultScanner} for a multi-entity read.    *    * @param hbaseConf HBase Configuration.    * @param conn HBase Connection.    * @param filterList filter list which will be applied to HBase Scan.    * @return the {@link ResultScanner} instance.    * @throws IOException if any exception is encountered while getting results.    */
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
comment|/**    * Parses the result retrieved from HBase backend and convert it into a    * {@link TimelineEntity} object.    *    * @param result Single row result of a Get/Scan.    * @return the<cite>TimelineEntity</cite> instance or null if the entity is    *     filtered.    * @throws IOException if any exception is encountered while parsing entity.    */
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
comment|/**    * Helper method for reading and deserializing {@link TimelineMetric} objects    * using the specified column prefix. The timeline metrics then are added to    * the given timeline entity.    *    * @param entity {@link TimelineEntity} object.    * @param result {@link Result} object retrieved from backend.    * @param columnPrefix Metric column prefix    * @throws IOException if any exception is encountered while reading metrics.    */
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
argument_list|,
name|StringKeyConverter
operator|.
name|getInstance
argument_list|()
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
comment|/**    * Checks whether the reader has been created to fetch single entity or    * multiple entities.    *    * @return true, if query is for single entity, false otherwise.    */
DECL|method|isSingleEntityRead ()
specifier|public
name|boolean
name|isSingleEntityRead
parameter_list|()
block|{
return|return
name|singleEntityRead
return|;
block|}
DECL|method|setTable (BaseTable<?> baseTable)
specifier|protected
name|void
name|setTable
parameter_list|(
name|BaseTable
argument_list|<
name|?
argument_list|>
name|baseTable
parameter_list|)
block|{
name|this
operator|.
name|table
operator|=
name|baseTable
expr_stmt|;
block|}
block|}
end_class

end_unit

