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
name|Set
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
name|FlowRunTable
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
DECL|method|FlowRunEntityReader (String userId, String clusterId, String flowName, Long flowRunId, String appId, String entityType, Long limit, Long createdTimeBegin, Long createdTimeEnd, Map<String, Set<String>> relatesTo, Map<String, Set<String>> isRelatedTo, Map<String, Object> infoFilters, Map<String, String> configFilters, Set<String> metricFilters, Set<String> eventFilters, TimelineFilterList confsToRetrieve, TimelineFilterList metricsToRetrieve, EnumSet<Field> fieldsToRetrieve)
specifier|public
name|FlowRunEntityReader
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
parameter_list|)
block|{
name|super
argument_list|(
name|userId
argument_list|,
name|clusterId
argument_list|,
name|flowName
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
name|entityType
argument_list|,
name|limit
argument_list|,
name|createdTimeBegin
argument_list|,
name|createdTimeEnd
argument_list|,
name|relatesTo
argument_list|,
name|isRelatedTo
argument_list|,
name|infoFilters
argument_list|,
name|configFilters
argument_list|,
name|metricFilters
argument_list|,
name|eventFilters
argument_list|,
literal|null
argument_list|,
name|metricsToRetrieve
argument_list|,
name|fieldsToRetrieve
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|FlowRunEntityReader (String userId, String clusterId, String flowName, Long flowRunId, String appId, String entityType, String entityId, TimelineFilterList confsToRetrieve, TimelineFilterList metricsToRetrieve, EnumSet<Field> fieldsToRetrieve)
specifier|public
name|FlowRunEntityReader
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
name|super
argument_list|(
name|userId
argument_list|,
name|clusterId
argument_list|,
name|flowName
argument_list|,
name|flowRunId
argument_list|,
name|appId
argument_list|,
name|entityType
argument_list|,
name|entityId
argument_list|,
literal|null
argument_list|,
name|metricsToRetrieve
argument_list|,
name|fieldsToRetrieve
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
name|clusterId
argument_list|,
literal|"clusterId shouldn't be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|userId
argument_list|,
literal|"userId shouldn't be null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|flowName
argument_list|,
literal|"flowName shouldn't be null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|singleEntityRead
condition|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|flowRunId
argument_list|,
literal|"flowRunId shouldn't be null"
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|!
name|singleEntityRead
condition|)
block|{
if|if
condition|(
name|fieldsToRetrieve
operator|==
literal|null
condition|)
block|{
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
if|if
condition|(
name|limit
operator|==
literal|null
operator|||
name|limit
operator|<
literal|0
condition|)
block|{
name|limit
operator|=
name|TimelineReader
operator|.
name|DEFAULT_LIMIT
expr_stmt|;
block|}
if|if
condition|(
name|createdTimeBegin
operator|==
literal|null
condition|)
block|{
name|createdTimeBegin
operator|=
name|DEFAULT_BEGIN_TIME
expr_stmt|;
block|}
if|if
condition|(
name|createdTimeEnd
operator|==
literal|null
condition|)
block|{
name|createdTimeEnd
operator|=
name|DEFAULT_END_TIME
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
block|}
annotation|@
name|Override
DECL|method|constructFilterListBasedOnFields ()
specifier|protected
name|FilterList
name|constructFilterListBasedOnFields
parameter_list|()
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
comment|// Metrics not required.
if|if
condition|(
operator|!
name|singleEntityRead
operator|&&
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
operator|!
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
name|Field
operator|.
name|ALL
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
name|infoColFamilyList
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
name|list
operator|.
name|addFilter
argument_list|(
name|infoColFamilyList
argument_list|)
expr_stmt|;
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
name|byte
index|[]
name|rowKey
init|=
name|FlowRunRowKey
operator|.
name|getRowKey
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowName
argument_list|,
name|flowRunId
argument_list|)
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
name|table
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
name|scan
operator|.
name|setRowPrefixFilter
argument_list|(
name|FlowRunRowKey
operator|.
name|getRowKeyPrefix
argument_list|(
name|clusterId
argument_list|,
name|userId
argument_list|,
name|flowName
argument_list|)
argument_list|)
expr_stmt|;
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
name|limit
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
return|return
name|table
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
name|userId
argument_list|)
expr_stmt|;
name|flowRun
operator|.
name|setName
argument_list|(
name|flowName
argument_list|)
expr_stmt|;
if|if
condition|(
name|singleEntityRead
condition|)
block|{
name|flowRun
operator|.
name|setRunId
argument_list|(
name|flowRunId
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
if|if
condition|(
operator|!
name|singleEntityRead
operator|&&
operator|(
name|flowRun
operator|.
name|getStartTime
argument_list|()
operator|<
name|createdTimeBegin
operator|||
name|flowRun
operator|.
name|getStartTime
argument_list|()
operator|>
name|createdTimeEnd
operator|)
condition|)
block|{
return|return
literal|null
return|;
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
comment|// read metrics
if|if
condition|(
name|singleEntityRead
operator|||
name|fieldsToRetrieve
operator|.
name|contains
argument_list|(
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

