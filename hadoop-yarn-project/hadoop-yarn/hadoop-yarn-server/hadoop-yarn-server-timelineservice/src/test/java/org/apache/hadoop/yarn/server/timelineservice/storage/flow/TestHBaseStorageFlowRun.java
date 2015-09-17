begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.flow
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
name|flow
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

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
name|HBaseConfiguration
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
name|HBaseTestingUtility
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
name|TableName
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
name|Admin
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
name|ConnectionFactory
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
name|client
operator|.
name|Table
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
name|util
operator|.
name|Bytes
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
name|TimelineEntityType
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
operator|.
name|Type
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
name|metrics
operator|.
name|ApplicationMetricsConstants
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
name|timeline
operator|.
name|GenericObjectMapper
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
name|HBaseTimelineWriterImpl
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
name|TimelineSchemaCreator
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
name|application
operator|.
name|ApplicationTable
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
name|apptoflow
operator|.
name|AppToFlowTable
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
name|ColumnHelper
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
name|Separator
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
name|TimelineWriterUtils
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
name|entity
operator|.
name|EntityTable
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
name|FlowActivityColumnFamily
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
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests the FlowRun and FlowActivity Tables  */
end_comment

begin_class
DECL|class|TestHBaseStorageFlowRun
specifier|public
class|class
name|TestHBaseStorageFlowRun
block|{
DECL|field|util
specifier|private
specifier|static
name|HBaseTestingUtility
name|util
decl_stmt|;
DECL|field|metric1
specifier|private
specifier|final
name|String
name|metric1
init|=
literal|"MAP_SLOT_MILLIS"
decl_stmt|;
DECL|field|metric2
specifier|private
specifier|final
name|String
name|metric2
init|=
literal|"HDFS_BYTES_READ"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupBeforeClass ()
specifier|public
specifier|static
name|void
name|setupBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|util
operator|=
operator|new
name|HBaseTestingUtility
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|util
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
literal|"hfile.format.version"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|util
operator|.
name|startMiniCluster
argument_list|()
expr_stmt|;
name|createSchema
argument_list|()
expr_stmt|;
block|}
DECL|method|createSchema ()
specifier|private
specifier|static
name|void
name|createSchema
parameter_list|()
throws|throws
name|IOException
block|{
name|TimelineSchemaCreator
operator|.
name|createAllTables
argument_list|(
name|util
operator|.
name|getConfiguration
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes 4 timeline entities belonging to one flow run through the    * {@link HBaseTimelineWriterImpl}    *    * Checks the flow run table contents    *    * The first entity has a created event, metrics and a finish event.    *    * The second entity has a created event and this is the entity with smallest    * start time. This should be the start time for the flow run.    *    * The third entity has a finish event and this is the entity with the max end    * time. This should be the end time for the flow run.    *    * The fourth entity has a created event which has a start time that is    * greater than min start time.    *    */
annotation|@
name|Test
DECL|method|testWriteFlowRunMinMax ()
specifier|public
name|void
name|testWriteFlowRunMinMax
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineEntities
name|te
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|TestFlowDataGenerator
operator|.
name|getEntity1
argument_list|()
argument_list|)
expr_stmt|;
name|HBaseTimelineWriterImpl
name|hbi
init|=
literal|null
decl_stmt|;
name|Configuration
name|c1
init|=
name|util
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|String
name|cluster
init|=
literal|"testWriteFlowRunMinMaxToHBase_cluster1"
decl_stmt|;
name|String
name|user
init|=
literal|"testWriteFlowRunMinMaxToHBase_user1"
decl_stmt|;
name|String
name|flow
init|=
literal|"testing_flowRun_flow_name"
decl_stmt|;
name|String
name|flowVersion
init|=
literal|"CF7022C10F1354"
decl_stmt|;
name|Long
name|runid
init|=
literal|1002345678919L
decl_stmt|;
name|String
name|appName
init|=
literal|"application_100000000000_1111"
decl_stmt|;
name|long
name|endTs
init|=
literal|1439750690000L
decl_stmt|;
name|TimelineEntity
name|entityMinStartTime
init|=
name|TestFlowDataGenerator
operator|.
name|getEntityMinStartTime
argument_list|()
decl_stmt|;
try|try
block|{
name|hbi
operator|=
operator|new
name|HBaseTimelineWriterImpl
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|hbi
operator|.
name|init
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|hbi
operator|.
name|write
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|flowVersion
argument_list|,
name|runid
argument_list|,
name|appName
argument_list|,
name|te
argument_list|)
expr_stmt|;
comment|// write another entity with the right min start time
name|te
operator|=
operator|new
name|TimelineEntities
argument_list|()
expr_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entityMinStartTime
argument_list|)
expr_stmt|;
name|appName
operator|=
literal|"application_100000000000_3333"
expr_stmt|;
name|hbi
operator|.
name|write
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|flowVersion
argument_list|,
name|runid
argument_list|,
name|appName
argument_list|,
name|te
argument_list|)
expr_stmt|;
comment|// writer another entity for max end time
name|TimelineEntity
name|entityMaxEndTime
init|=
name|TestFlowDataGenerator
operator|.
name|getEntityMaxEndTime
argument_list|(
name|endTs
argument_list|)
decl_stmt|;
name|te
operator|=
operator|new
name|TimelineEntities
argument_list|()
expr_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entityMaxEndTime
argument_list|)
expr_stmt|;
name|appName
operator|=
literal|"application_100000000000_4444"
expr_stmt|;
name|hbi
operator|.
name|write
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|flowVersion
argument_list|,
name|runid
argument_list|,
name|appName
argument_list|,
name|te
argument_list|)
expr_stmt|;
comment|// writer another entity with greater start time
name|TimelineEntity
name|entityGreaterStartTime
init|=
name|TestFlowDataGenerator
operator|.
name|getEntityGreaterStartTime
argument_list|()
decl_stmt|;
name|te
operator|=
operator|new
name|TimelineEntities
argument_list|()
expr_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entityGreaterStartTime
argument_list|)
expr_stmt|;
name|appName
operator|=
literal|"application_1000000000000000_2222"
expr_stmt|;
name|hbi
operator|.
name|write
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|flowVersion
argument_list|,
name|runid
argument_list|,
name|appName
argument_list|,
name|te
argument_list|)
expr_stmt|;
comment|// flush everything to hbase
name|hbi
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|hbi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Connection
name|conn
init|=
name|ConnectionFactory
operator|.
name|createConnection
argument_list|(
name|c1
argument_list|)
decl_stmt|;
comment|// check in flow run table
name|Table
name|table1
init|=
name|conn
operator|.
name|getTable
argument_list|(
name|TableName
operator|.
name|valueOf
argument_list|(
name|FlowRunTable
operator|.
name|DEFAULT_TABLE_NAME
argument_list|)
argument_list|)
decl_stmt|;
comment|// scan the table and see that we get back the right min and max
comment|// timestamps
name|byte
index|[]
name|startRow
init|=
name|FlowRunRowKey
operator|.
name|getRowKey
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|runid
argument_list|)
decl_stmt|;
name|Get
name|g
init|=
operator|new
name|Get
argument_list|(
name|startRow
argument_list|)
decl_stmt|;
name|g
operator|.
name|addColumn
argument_list|(
name|FlowRunColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|,
name|FlowRunColumn
operator|.
name|MIN_START_TIME
operator|.
name|getColumnQualifierBytes
argument_list|()
argument_list|)
expr_stmt|;
name|g
operator|.
name|addColumn
argument_list|(
name|FlowRunColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|,
name|FlowRunColumn
operator|.
name|MAX_END_TIME
operator|.
name|getColumnQualifierBytes
argument_list|()
argument_list|)
expr_stmt|;
name|Result
name|r1
init|=
name|table1
operator|.
name|get
argument_list|(
name|g
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|r1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|r1
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|values
init|=
name|r1
operator|.
name|getFamilyMap
argument_list|(
name|FlowRunColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|r1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Long
name|starttime
init|=
operator|(
name|Long
operator|)
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|FlowRunColumn
operator|.
name|MIN_START_TIME
operator|.
name|getColumnQualifierBytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Long
name|expmin
init|=
name|entityMinStartTime
operator|.
name|getCreatedTime
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expmin
argument_list|,
name|starttime
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|endTs
argument_list|,
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|FlowRunColumn
operator|.
name|MAX_END_TIME
operator|.
name|getColumnQualifierBytes
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isFlowRunRowKeyCorrect (byte[] rowKey, String cluster, String user, String flow, Long runid)
name|boolean
name|isFlowRunRowKeyCorrect
parameter_list|(
name|byte
index|[]
name|rowKey
parameter_list|,
name|String
name|cluster
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|flow
parameter_list|,
name|Long
name|runid
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|rowKeyComponents
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|rowKey
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|rowKeyComponents
operator|.
name|length
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cluster
argument_list|,
name|Bytes
operator|.
name|toString
argument_list|(
name|rowKeyComponents
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|Bytes
operator|.
name|toString
argument_list|(
name|rowKeyComponents
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flow
argument_list|,
name|Bytes
operator|.
name|toString
argument_list|(
name|rowKeyComponents
index|[
literal|2
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimelineWriterUtils
operator|.
name|invert
argument_list|(
name|runid
argument_list|)
argument_list|,
name|Bytes
operator|.
name|toLong
argument_list|(
name|rowKeyComponents
index|[
literal|3
index|]
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Writes two application entities of the same flow run. Each application has    * two metrics: slot millis and hdfs bytes read. Each metric has values at two    * timestamps.    *    * Checks the metric values of the flow in the flow run table. Flow metric    * values should be the sum of individual metric values that belong to the    * latest timestamp for that metric    */
annotation|@
name|Test
DECL|method|testWriteFlowRunMetricsOneFlow ()
specifier|public
name|void
name|testWriteFlowRunMetricsOneFlow
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|cluster
init|=
literal|"testWriteFlowRunMetricsOneFlow_cluster1"
decl_stmt|;
name|String
name|user
init|=
literal|"testWriteFlowRunMetricsOneFlow_user1"
decl_stmt|;
name|String
name|flow
init|=
literal|"testing_flowRun_metrics_flow_name"
decl_stmt|;
name|String
name|flowVersion
init|=
literal|"CF7022C10F1354"
decl_stmt|;
name|Long
name|runid
init|=
literal|1002345678919L
decl_stmt|;
name|TimelineEntities
name|te
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|TimelineEntity
name|entityApp1
init|=
name|TestFlowDataGenerator
operator|.
name|getEntityMetricsApp1
argument_list|()
decl_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entityApp1
argument_list|)
expr_stmt|;
name|HBaseTimelineWriterImpl
name|hbi
init|=
literal|null
decl_stmt|;
name|Configuration
name|c1
init|=
name|util
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
try|try
block|{
name|hbi
operator|=
operator|new
name|HBaseTimelineWriterImpl
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|hbi
operator|.
name|init
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|String
name|appName
init|=
literal|"application_11111111111111_1111"
decl_stmt|;
name|hbi
operator|.
name|write
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|flowVersion
argument_list|,
name|runid
argument_list|,
name|appName
argument_list|,
name|te
argument_list|)
expr_stmt|;
comment|// write another application with same metric to this flow
name|te
operator|=
operator|new
name|TimelineEntities
argument_list|()
expr_stmt|;
name|TimelineEntity
name|entityApp2
init|=
name|TestFlowDataGenerator
operator|.
name|getEntityMetricsApp2
argument_list|()
decl_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entityApp2
argument_list|)
expr_stmt|;
name|appName
operator|=
literal|"application_11111111111111_2222"
expr_stmt|;
name|hbi
operator|.
name|write
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|flowVersion
argument_list|,
name|runid
argument_list|,
name|appName
argument_list|,
name|te
argument_list|)
expr_stmt|;
name|hbi
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|hbi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// check flow run
name|checkFlowRunTable
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|runid
argument_list|,
name|c1
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFlowRunTable (String cluster, String user, String flow, long runid, Configuration c1)
specifier|private
name|void
name|checkFlowRunTable
parameter_list|(
name|String
name|cluster
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|flow
parameter_list|,
name|long
name|runid
parameter_list|,
name|Configuration
name|c1
parameter_list|)
throws|throws
name|IOException
block|{
name|Scan
name|s
init|=
operator|new
name|Scan
argument_list|()
decl_stmt|;
name|s
operator|.
name|addFamily
argument_list|(
name|FlowRunColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|startRow
init|=
name|FlowRunRowKey
operator|.
name|getRowKey
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|runid
argument_list|)
decl_stmt|;
name|s
operator|.
name|setStartRow
argument_list|(
name|startRow
argument_list|)
expr_stmt|;
name|String
name|clusterStop
init|=
name|cluster
operator|+
literal|"1"
decl_stmt|;
name|byte
index|[]
name|stopRow
init|=
name|FlowRunRowKey
operator|.
name|getRowKey
argument_list|(
name|clusterStop
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|runid
argument_list|)
decl_stmt|;
name|s
operator|.
name|setStopRow
argument_list|(
name|stopRow
argument_list|)
expr_stmt|;
name|Connection
name|conn
init|=
name|ConnectionFactory
operator|.
name|createConnection
argument_list|(
name|c1
argument_list|)
decl_stmt|;
name|Table
name|table1
init|=
name|conn
operator|.
name|getTable
argument_list|(
name|TableName
operator|.
name|valueOf
argument_list|(
name|FlowRunTable
operator|.
name|DEFAULT_TABLE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|ResultScanner
name|scanner
init|=
name|table1
operator|.
name|getScanner
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|int
name|rowCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Result
name|result
range|:
name|scanner
control|)
block|{
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|values
init|=
name|result
operator|.
name|getFamilyMap
argument_list|(
name|FlowRunColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|rowCount
operator|++
expr_stmt|;
comment|// check metric1
name|byte
index|[]
name|q
init|=
name|ColumnHelper
operator|.
name|getColumnQualifier
argument_list|(
name|FlowRunColumnPrefix
operator|.
name|METRIC
operator|.
name|getColumnPrefixBytes
argument_list|()
argument_list|,
name|metric1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|containsKey
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|141
argument_list|,
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|q
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// check metric2
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|q
operator|=
name|ColumnHelper
operator|.
name|getColumnQualifier
argument_list|(
name|FlowRunColumnPrefix
operator|.
name|METRIC
operator|.
name|getColumnPrefixBytes
argument_list|()
argument_list|,
name|metric2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|values
operator|.
name|containsKey
argument_list|(
name|q
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|57
argument_list|,
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|q
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rowCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDownAfterClass ()
specifier|public
specifier|static
name|void
name|tearDownAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|util
operator|.
name|shutdownMiniCluster
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

