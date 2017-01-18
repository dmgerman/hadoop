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
name|Get
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
name|client
operator|.
name|Scan
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
name|BinaryComparator
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
name|BinaryPrefixComparator
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
name|CompareFilter
operator|.
name|CompareOp
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
name|FamilyFilter
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
name|hbase
operator|.
name|filter
operator|.
name|FilterList
operator|.
name|Operator
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
name|PageFilter
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
name|QualifierFilter
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
name|FlowRunEntity
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
name|TimelineFilterUtils
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
name|HBaseTimelineStorageUtils
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
name|RowKeyPrefix
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
name|flow
operator|.
name|FlowRunColumn
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
name|flow
operator|.
name|FlowRunColumnFamily
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
name|flow
operator|.
name|FlowRunColumnPrefix
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
name|flow
operator|.
name|FlowRunRowKey
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
name|flow
operator|.
name|FlowRunRowKeyPrefix
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
name|flow
operator|.
name|FlowRunTable
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
name|webapp
operator|.
name|BadRequestException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * Timeline entity reader for flow run entities that are stored in the flow run  * table.  */
end_comment

begin_class
DECL|class|FlowRunEntityReader
class|class
name|FlowRunEntityReader
extends|extends
name|TimelineEntityReader
block|{
DECL|field|FLOW_RUN_TABLE
specifier|private
specifier|static
specifier|final
name|FlowRunTable
name|FLOW_RUN_TABLE
init|=
operator|new
name|FlowRunTable
argument_list|()
decl_stmt|;
DECL|method|FlowRunEntityReader (TimelineReaderContext ctxt, TimelineEntityFilters entityFilters, TimelineDataToRetrieve toRetrieve)
specifier|public
name|FlowRunEntityReader
parameter_list|(
name|TimelineReaderContext
name|ctxt
parameter_list|,
name|TimelineEntityFilters
name|entityFilters
parameter_list|,
name|TimelineDataToRetrieve
name|toRetrieve
parameter_list|)
block|{
name|super
argument_list|(
name|ctxt
argument_list|,
name|entityFilters
argument_list|,
name|toRetrieve
argument_list|)
expr_stmt|;
block|}
DECL|method|FlowRunEntityReader (TimelineReaderContext ctxt, TimelineDataToRetrieve toRetrieve)
specifier|public
name|FlowRunEntityReader
parameter_list|(
name|TimelineReaderContext
name|ctxt
parameter_list|,
name|TimelineDataToRetrieve
name|toRetrieve
parameter_list|)
block|{
name|super
argument_list|(
name|ctxt
argument_list|,
name|toRetrieve
argument_list|)
expr_stmt|;
block|}
comment|/**    * Uses the {@link FlowRunTable}.    */
annotation|@
name|Override
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
name|FLOW_RUN_TABLE
return|;
block|}
annotation|@
name|Override
DECL|method|validateParams ()
specifier|protected
name|void
name|validateParams
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getContext
argument_list|()
argument_list|,
literal|"context shouldn't be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getDataToRetrieve
argument_list|()
argument_list|,
literal|"data to retrieve shouldn't be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getContext
argument_list|()
operator|.
name|getClusterId
argument_list|()
argument_list|,
literal|"clusterId shouldn't be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getContext
argument_list|()
operator|.
name|getUserId
argument_list|()
argument_list|,
literal|"userId shouldn't be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getContext
argument_list|()
operator|.
name|getFlowName
argument_list|()
argument_list|,
literal|"flowName shouldn't be null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSingleEntityRead
argument_list|()
condition|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|getContext
argument_list|()
operator|.
name|getFlowRunId
argument_list|()
argument_list|,
literal|"flowRunId shouldn't be null"
argument_list|)
expr_stmt|;
block|}
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|fieldsToRetrieve
init|=
name|getDataToRetrieve
argument_list|()
operator|.
name|getFieldsToRetrieve
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isSingleEntityRead
argument_list|()
operator|&&
name|fieldsToRetrieve
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Field
name|field
range|:
name|fieldsToRetrieve
control|)
block|{
if|if
condition|(
name|field
operator|!=
name|Field
operator|.
name|ALL
operator|&&
name|field
operator|!=
name|Field
operator|.
name|METRICS
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Invalid field "
operator|+
name|field
operator|+
literal|" specified while querying flow runs."
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|augmentParams (Configuration hbaseConf, Connection conn)
specifier|protected
name|void
name|augmentParams
parameter_list|(
name|Configuration
name|hbaseConf
parameter_list|,
name|Connection
name|conn
parameter_list|)
block|{
comment|// Add metrics to fields to retrieve if metricsToRetrieve is specified.
name|getDataToRetrieve
argument_list|()
operator|.
name|addFieldsBasedOnConfsAndMetricsToRetrieve
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isSingleEntityRead
argument_list|()
condition|)
block|{
name|createFiltersIfNull
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|constructFilterListBasedOnFilters ()
specifier|protected
name|FilterList
name|constructFilterListBasedOnFilters
parameter_list|()
throws|throws
name|IOException
block|{
name|FilterList
name|listBasedOnFilters
init|=
operator|new
name|FilterList
argument_list|()
decl_stmt|;
comment|// Filter based on created time range.
name|Long
name|createdTimeBegin
init|=
name|getFilters
argument_list|()
operator|.
name|getCreatedTimeBegin
argument_list|()
decl_stmt|;
name|Long
name|createdTimeEnd
init|=
name|getFilters
argument_list|()
operator|.
name|getCreatedTimeEnd
argument_list|()
decl_stmt|;
if|if
condition|(
name|createdTimeBegin
operator|!=
literal|0
operator|||
name|createdTimeEnd
operator|!=
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|listBasedOnFilters
operator|.
name|addFilter
argument_list|(
name|TimelineFilterUtils
operator|.
name|createSingleColValueFiltersByRange
argument_list|(
name|FlowRunColumn
operator|.
name|MIN_START_TIME
argument_list|,
name|createdTimeBegin
argument_list|,
name|createdTimeEnd
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Filter based on metric filters.
name|TimelineFilterList
name|metricFilters
init|=
name|getFilters
argument_list|()
operator|.
name|getMetricFilters
argument_list|()
decl_stmt|;
if|if
condition|(
name|metricFilters
operator|!=
literal|null
operator|&&
operator|!
name|metricFilters
operator|.
name|getFilterList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|listBasedOnFilters
operator|.
name|addFilter
argument_list|(
name|TimelineFilterUtils
operator|.
name|createHBaseFilterList
argument_list|(
name|FlowRunColumnPrefix
operator|.
name|METRIC
argument_list|,
name|metricFilters
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|listBasedOnFilters
return|;
block|}
comment|/**    * Add {@link QualifierFilter} filters to filter list for each column of flow    * run table.    *    * @return filter list to which qualifier filters have been added.    */
DECL|method|updateFixedColumns ()
specifier|private
name|FilterList
name|updateFixedColumns
parameter_list|()
block|{
name|FilterList
name|columnsList
init|=
operator|new
name|FilterList
argument_list|(
name|Operator
operator|.
name|MUST_PASS_ONE
argument_list|)
decl_stmt|;
for|for
control|(
name|FlowRunColumn
name|column
range|:
name|FlowRunColumn
operator|.
name|values
argument_list|()
control|)
block|{
name|columnsList
operator|.
name|addFilter
argument_list|(
operator|new
name|QualifierFilter
argument_list|(
name|CompareOp
operator|.
name|EQUAL
argument_list|,
operator|new
name|BinaryComparator
argument_list|(
name|column
operator|.
name|getColumnQualifierBytes
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|columnsList
return|;
block|}
annotation|@
name|Override
DECL|method|constructFilterListBasedOnFields ()
specifier|protected
name|FilterList
name|constructFilterListBasedOnFields
parameter_list|()
throws|throws
name|IOException
block|{
name|FilterList
name|list
init|=
operator|new
name|FilterList
argument_list|(
name|Operator
operator|.
name|MUST_PASS_ONE
argument_list|)
decl_stmt|;
comment|// By default fetch everything in INFO column family.
name|FamilyFilter
name|infoColumnFamily
init|=
operator|new
name|FamilyFilter
argument_list|(
name|CompareOp
operator|.
name|EQUAL
argument_list|,
operator|new
name|BinaryComparator
argument_list|(
name|FlowRunColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|TimelineDataToRetrieve
name|dataToRetrieve
init|=
name|getDataToRetrieve
argument_list|()
decl_stmt|;
comment|// If multiple entities have to be retrieved, check if metrics have to be
comment|// retrieved and if not, add a filter so that metrics can be excluded.
comment|// Metrics are always returned if we are reading a single entity.
if|if
condition|(
operator|!
name|isSingleEntityRead
argument_list|()
operator|&&
operator|!
name|hasField
argument_list|(
name|dataToRetrieve
operator|.
name|getFieldsToRetrieve
argument_list|()
argument_list|,
name|Field
operator|.
name|METRICS
argument_list|)
condition|)
block|{
name|FilterList
name|infoColFamilyList
init|=
operator|new
name|FilterList
argument_list|(
name|Operator
operator|.
name|MUST_PASS_ONE
argument_list|)
decl_stmt|;
name|infoColFamilyList
operator|.
name|addFilter
argument_list|(
name|infoColumnFamily
argument_list|)
expr_stmt|;
name|infoColFamilyList
operator|.
name|addFilter
argument_list|(
operator|new
name|QualifierFilter
argument_list|(
name|CompareOp
operator|.
name|NOT_EQUAL
argument_list|,
operator|new
name|BinaryPrefixComparator
argument_list|(
name|FlowRunColumnPrefix
operator|.
name|METRIC
operator|.
name|getColumnPrefixBytes
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|list
operator|.
name|addFilter
argument_list|(
name|infoColFamilyList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check if metricsToRetrieve are specified and if they are, create a
comment|// filter list for info column family by adding flow run tables columns
comment|// and a list for metrics to retrieve. Pls note that fieldsToRetrieve
comment|// will have METRICS added to it if metricsToRetrieve are specified
comment|// (in augmentParams()).
name|TimelineFilterList
name|metricsToRetrieve
init|=
name|dataToRetrieve
operator|.
name|getMetricsToRetrieve
argument_list|()
decl_stmt|;
if|if
condition|(
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
name|FilterList
name|infoColFamilyList
init|=
operator|new
name|FilterList
argument_list|()
decl_stmt|;
name|infoColFamilyList
operator|.
name|addFilter
argument_list|(
name|infoColumnFamily
argument_list|)
expr_stmt|;
name|FilterList
name|columnsList
init|=
name|updateFixedColumns
argument_list|()
decl_stmt|;
name|columnsList
operator|.
name|addFilter
argument_list|(
name|TimelineFilterUtils
operator|.
name|createHBaseFilterList
argument_list|(
name|FlowRunColumnPrefix
operator|.
name|METRIC
argument_list|,
name|metricsToRetrieve
argument_list|)
argument_list|)
expr_stmt|;
name|infoColFamilyList
operator|.
name|addFilter
argument_list|(
name|columnsList
argument_list|)
expr_stmt|;
name|list
operator|.
name|addFilter
argument_list|(
name|infoColFamilyList
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
annotation|@
name|Override
DECL|method|getResult (Configuration hbaseConf, Connection conn, FilterList filterList)
specifier|protected
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
block|{
name|TimelineReaderContext
name|context
init|=
name|getContext
argument_list|()
decl_stmt|;
name|FlowRunRowKey
name|flowRunRowKey
init|=
operator|new
name|FlowRunRowKey
argument_list|(
name|context
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|context
operator|.
name|getUserId
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|rowKey
init|=
name|flowRunRowKey
operator|.
name|getRowKey
argument_list|()
decl_stmt|;
name|Get
name|get
init|=
operator|new
name|Get
argument_list|(
name|rowKey
argument_list|)
decl_stmt|;
name|get
operator|.
name|setMaxVersions
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
if|if
condition|(
name|filterList
operator|!=
literal|null
operator|&&
operator|!
name|filterList
operator|.
name|getFilters
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|get
operator|.
name|setFilter
argument_list|(
name|filterList
argument_list|)
expr_stmt|;
block|}
return|return
name|getTable
argument_list|()
operator|.
name|getResult
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|,
name|get
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getResults (Configuration hbaseConf, Connection conn, FilterList filterList)
specifier|protected
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
block|{
name|Scan
name|scan
init|=
operator|new
name|Scan
argument_list|()
decl_stmt|;
name|TimelineReaderContext
name|context
init|=
name|getContext
argument_list|()
decl_stmt|;
name|RowKeyPrefix
argument_list|<
name|FlowRunRowKey
argument_list|>
name|flowRunRowKeyPrefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getFilters
argument_list|()
operator|.
name|getFromId
argument_list|()
operator|==
literal|null
condition|)
block|{
name|flowRunRowKeyPrefix
operator|=
operator|new
name|FlowRunRowKeyPrefix
argument_list|(
name|context
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|context
operator|.
name|getUserId
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
name|scan
operator|.
name|setRowPrefixFilter
argument_list|(
name|flowRunRowKeyPrefix
operator|.
name|getRowKeyPrefix
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FlowRunRowKey
name|flowRunRowKey
init|=
operator|new
name|FlowRunRowKey
argument_list|(
name|context
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|context
operator|.
name|getUserId
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|getFilters
argument_list|()
operator|.
name|getFromId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// set start row
name|scan
operator|.
name|setStartRow
argument_list|(
name|flowRunRowKey
operator|.
name|getRowKey
argument_list|()
argument_list|)
expr_stmt|;
comment|// get the bytes for stop row
name|flowRunRowKeyPrefix
operator|=
operator|new
name|FlowRunRowKeyPrefix
argument_list|(
name|context
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|context
operator|.
name|getUserId
argument_list|()
argument_list|,
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
comment|// set stop row
name|scan
operator|.
name|setStopRow
argument_list|(
name|HBaseTimelineStorageUtils
operator|.
name|calculateTheClosestNextRowKeyForPrefix
argument_list|(
name|flowRunRowKeyPrefix
operator|.
name|getRowKeyPrefix
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FilterList
name|newList
init|=
operator|new
name|FilterList
argument_list|()
decl_stmt|;
name|newList
operator|.
name|addFilter
argument_list|(
operator|new
name|PageFilter
argument_list|(
name|getFilters
argument_list|()
operator|.
name|getLimit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|filterList
operator|!=
literal|null
operator|&&
operator|!
name|filterList
operator|.
name|getFilters
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|newList
operator|.
name|addFilter
argument_list|(
name|filterList
argument_list|)
expr_stmt|;
block|}
name|scan
operator|.
name|setFilter
argument_list|(
name|newList
argument_list|)
expr_stmt|;
name|scan
operator|.
name|setMaxVersions
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
return|return
name|getTable
argument_list|()
operator|.
name|getResultScanner
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|,
name|scan
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseEntity (Result result)
specifier|protected
name|TimelineEntity
name|parseEntity
parameter_list|(
name|Result
name|result
parameter_list|)
throws|throws
name|IOException
block|{
name|TimelineReaderContext
name|context
init|=
name|getContext
argument_list|()
decl_stmt|;
name|FlowRunEntity
name|flowRun
init|=
operator|new
name|FlowRunEntity
argument_list|()
decl_stmt|;
name|flowRun
operator|.
name|setUser
argument_list|(
name|context
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|flowRun
operator|.
name|setName
argument_list|(
name|context
operator|.
name|getFlowName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSingleEntityRead
argument_list|()
condition|)
block|{
name|flowRun
operator|.
name|setRunId
argument_list|(
name|context
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FlowRunRowKey
name|rowKey
init|=
name|FlowRunRowKey
operator|.
name|parseRowKey
argument_list|(
name|result
operator|.
name|getRow
argument_list|()
argument_list|)
decl_stmt|;
name|flowRun
operator|.
name|setRunId
argument_list|(
name|rowKey
operator|.
name|getFlowRunId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// read the start time
name|Long
name|startTime
init|=
operator|(
name|Long
operator|)
name|FlowRunColumn
operator|.
name|MIN_START_TIME
operator|.
name|readResult
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|startTime
operator|!=
literal|null
condition|)
block|{
name|flowRun
operator|.
name|setStartTime
argument_list|(
name|startTime
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// read the end time if available
name|Long
name|endTime
init|=
operator|(
name|Long
operator|)
name|FlowRunColumn
operator|.
name|MAX_END_TIME
operator|.
name|readResult
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|endTime
operator|!=
literal|null
condition|)
block|{
name|flowRun
operator|.
name|setMaxEndTime
argument_list|(
name|endTime
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// read the flow version
name|String
name|version
init|=
operator|(
name|String
operator|)
name|FlowRunColumn
operator|.
name|FLOW_VERSION
operator|.
name|readResult
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|flowRun
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
comment|// read metrics if its a single entity query or if METRICS are part of
comment|// fieldsToRetrieve.
if|if
condition|(
name|isSingleEntityRead
argument_list|()
operator|||
name|hasField
argument_list|(
name|getDataToRetrieve
argument_list|()
operator|.
name|getFieldsToRetrieve
argument_list|()
argument_list|,
name|Field
operator|.
name|METRICS
argument_list|)
condition|)
block|{
name|readMetrics
argument_list|(
name|flowRun
argument_list|,
name|result
argument_list|,
name|FlowRunColumnPrefix
operator|.
name|METRIC
argument_list|)
expr_stmt|;
block|}
comment|// set the id
name|flowRun
operator|.
name|setId
argument_list|(
name|flowRun
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|flowRun
return|;
block|}
block|}
end_class

end_unit

