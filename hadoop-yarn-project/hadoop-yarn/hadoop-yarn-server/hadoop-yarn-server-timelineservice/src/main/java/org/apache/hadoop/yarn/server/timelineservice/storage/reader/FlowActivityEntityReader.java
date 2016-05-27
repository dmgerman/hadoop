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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|FlowActivityEntity
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
name|LongKeyConverter
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
name|FlowActivityColumnPrefix
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
name|FlowActivityRowKey
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
name|FlowActivityTable
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
comment|/**  * Timeline entity reader for flow activity entities that are stored in the  * flow activity table.  */
end_comment

begin_class
DECL|class|FlowActivityEntityReader
class|class
name|FlowActivityEntityReader
extends|extends
name|TimelineEntityReader
block|{
DECL|field|FLOW_ACTIVITY_TABLE
specifier|private
specifier|static
specifier|final
name|FlowActivityTable
name|FLOW_ACTIVITY_TABLE
init|=
operator|new
name|FlowActivityTable
argument_list|()
decl_stmt|;
DECL|method|FlowActivityEntityReader (TimelineReaderContext ctxt, TimelineEntityFilters entityFilters, TimelineDataToRetrieve toRetrieve)
specifier|public
name|FlowActivityEntityReader
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
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|FlowActivityEntityReader (TimelineReaderContext ctxt, TimelineDataToRetrieve toRetrieve)
specifier|public
name|FlowActivityEntityReader
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
comment|/**    * Uses the {@link FlowActivityTable}.    */
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
name|FLOW_ACTIVITY_TABLE
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
operator|.
name|getClusterId
argument_list|()
argument_list|,
literal|"clusterId shouldn't be null"
argument_list|)
expr_stmt|;
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
throws|throws
name|IOException
block|{
name|createFiltersIfNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|constructFilterListBasedOnFilters ()
specifier|protected
name|FilterList
name|constructFilterListBasedOnFilters
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|constructFilterListBasedOnFields ()
specifier|protected
name|FilterList
name|constructFilterListBasedOnFields
parameter_list|()
block|{
return|return
literal|null
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"we don't support a single entity query"
argument_list|)
throw|;
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
name|String
name|clusterId
init|=
name|getContext
argument_list|()
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
if|if
condition|(
name|getFilters
argument_list|()
operator|.
name|getCreatedTimeBegin
argument_list|()
operator|==
literal|0L
operator|&&
name|getFilters
argument_list|()
operator|.
name|getCreatedTimeEnd
argument_list|()
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
comment|// All records have to be chosen.
name|scan
operator|.
name|setRowPrefixFilter
argument_list|(
name|FlowActivityRowKey
operator|.
name|getRowKeyPrefix
argument_list|(
name|clusterId
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scan
operator|.
name|setStartRow
argument_list|(
name|FlowActivityRowKey
operator|.
name|getRowKeyPrefix
argument_list|(
name|clusterId
argument_list|,
name|getFilters
argument_list|()
operator|.
name|getCreatedTimeEnd
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|scan
operator|.
name|setStopRow
argument_list|(
name|FlowActivityRowKey
operator|.
name|getRowKeyPrefix
argument_list|(
name|clusterId
argument_list|,
operator|(
name|getFilters
argument_list|()
operator|.
name|getCreatedTimeBegin
argument_list|()
operator|<=
literal|0
condition|?
literal|0
else|:
operator|(
name|getFilters
argument_list|()
operator|.
name|getCreatedTimeBegin
argument_list|()
operator|-
literal|1
operator|)
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// use the page filter to limit the result to the page size
comment|// the scanner may still return more than the limit; therefore we need to
comment|// read the right number as we iterate
name|scan
operator|.
name|setFilter
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
name|FlowActivityRowKey
name|rowKey
init|=
name|FlowActivityRowKey
operator|.
name|parseRowKey
argument_list|(
name|result
operator|.
name|getRow
argument_list|()
argument_list|)
decl_stmt|;
name|Long
name|time
init|=
name|rowKey
operator|.
name|getDayTimestamp
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|rowKey
operator|.
name|getUserId
argument_list|()
decl_stmt|;
name|String
name|flowName
init|=
name|rowKey
operator|.
name|getFlowName
argument_list|()
decl_stmt|;
name|FlowActivityEntity
name|flowActivity
init|=
operator|new
name|FlowActivityEntity
argument_list|(
name|getContext
argument_list|()
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|time
argument_list|,
name|user
argument_list|,
name|flowName
argument_list|)
decl_stmt|;
comment|// set the id
name|flowActivity
operator|.
name|setId
argument_list|(
name|flowActivity
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// get the list of run ids along with the version that are associated with
comment|// this flow on this day
name|Map
argument_list|<
name|Long
argument_list|,
name|Object
argument_list|>
name|runIdsMap
init|=
name|FlowActivityColumnPrefix
operator|.
name|RUN_ID
operator|.
name|readResults
argument_list|(
name|result
argument_list|,
name|LongKeyConverter
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
name|Long
argument_list|,
name|Object
argument_list|>
name|e
range|:
name|runIdsMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Long
name|runId
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|version
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|getValue
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
name|user
argument_list|)
expr_stmt|;
name|flowRun
operator|.
name|setName
argument_list|(
name|flowName
argument_list|)
expr_stmt|;
name|flowRun
operator|.
name|setRunId
argument_list|(
name|runId
argument_list|)
expr_stmt|;
name|flowRun
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
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
name|flowActivity
operator|.
name|addFlowRun
argument_list|(
name|flowRun
argument_list|)
expr_stmt|;
block|}
return|return
name|flowActivity
return|;
block|}
block|}
end_class

end_unit

