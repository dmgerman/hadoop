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
DECL|class|TestHBaseStorageFlowActivity
specifier|public
class|class
name|TestHBaseStorageFlowActivity
block|{
DECL|field|util
specifier|private
specifier|static
name|HBaseTestingUtility
name|util
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
comment|/**    * Writes 4 timeline entities belonging to one flow run through the    * {@link HBaseTimelineWriterImpl}    *    * Checks the flow run table contents    *    * The first entity has a created event, metrics and a finish event.    *    * The second entity has a created event and this is the entity with smallest    * start time. This should be the start time for the flow run.    *    * The third entity has a finish event and this is the entity with the max end    * time. This should be the end time for the flow run.    *    * The fourth entity has a created event which has a start time that is    * greater than min start time.    *    * The test also checks in the flow activity table that one entry has been    * made for all of these 4 application entities since they belong to the same    * flow run.    */
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
comment|// check in flow activity table
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
name|FlowActivityTable
operator|.
name|DEFAULT_TABLE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|startRow
init|=
name|FlowActivityRowKey
operator|.
name|getRowKey
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
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
name|FlowActivityColumnFamily
operator|.
name|INFO
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|row
init|=
name|r1
operator|.
name|getRow
argument_list|()
decl_stmt|;
name|FlowActivityRowKey
name|flowActivityRowKey
init|=
name|FlowActivityRowKey
operator|.
name|parseRowKey
argument_list|(
name|row
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|flowActivityRowKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cluster
argument_list|,
name|flowActivityRowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|flowActivityRowKey
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flow
argument_list|,
name|flowActivityRowKey
operator|.
name|getFlowId
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|dayTs
init|=
name|TimelineWriterUtils
operator|.
name|getTopOfTheDayTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dayTs
argument_list|,
name|flowActivityRowKey
operator|.
name|getDayTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkFlowActivityRunId
argument_list|(
name|runid
argument_list|,
name|flowVersion
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write 1 application entity and checks the record for today in the flow    * activity table    */
annotation|@
name|Test
DECL|method|testWriteFlowActivityOneFlow ()
specifier|public
name|void
name|testWriteFlowActivityOneFlow
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|cluster
init|=
literal|"testWriteFlowActivityOneFlow_cluster1"
decl_stmt|;
name|String
name|user
init|=
literal|"testWriteFlowActivityOneFlow_user1"
decl_stmt|;
name|String
name|flow
init|=
literal|"flow_activity_test_flow_name"
decl_stmt|;
name|String
name|flowVersion
init|=
literal|"A122110F135BC4"
decl_stmt|;
name|Long
name|runid
init|=
literal|1001111178919L
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
name|getFlowApp1
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
literal|"application_1111999999_1234"
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
comment|// check flow activity
name|checkFlowActivityTable
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
name|c1
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFlowActivityTable (String cluster, String user, String flow, String flowVersion, Long runid, Configuration c1)
specifier|private
name|void
name|checkFlowActivityTable
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
name|String
name|flowVersion
parameter_list|,
name|Long
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
name|FlowActivityColumnFamily
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
name|FlowActivityRowKey
operator|.
name|getRowKey
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
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
name|FlowActivityRowKey
operator|.
name|getRowKey
argument_list|(
name|clusterStop
argument_list|,
name|user
argument_list|,
name|flow
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
name|FlowActivityTable
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
name|FlowActivityColumnFamily
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
name|byte
index|[]
name|row
init|=
name|result
operator|.
name|getRow
argument_list|()
decl_stmt|;
name|FlowActivityRowKey
name|flowActivityRowKey
init|=
name|FlowActivityRowKey
operator|.
name|parseRowKey
argument_list|(
name|row
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|flowActivityRowKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cluster
argument_list|,
name|flowActivityRowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|flowActivityRowKey
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flow
argument_list|,
name|flowActivityRowKey
operator|.
name|getFlowId
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|dayTs
init|=
name|TimelineWriterUtils
operator|.
name|getTopOfTheDayTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dayTs
argument_list|,
name|flowActivityRowKey
operator|.
name|getDayTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkFlowActivityRunId
argument_list|(
name|runid
argument_list|,
name|flowVersion
argument_list|,
name|values
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
comment|/**    * Writes 3 applications each with a different run id and version for the same    * {cluster, user, flow}    *    * They should be getting inserted into one record in the flow activity table    * with 3 columns, one per run id    */
annotation|@
name|Test
DECL|method|testFlowActivityTableOneFlowMultipleRunIds ()
specifier|public
name|void
name|testFlowActivityTableOneFlowMultipleRunIds
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|cluster
init|=
literal|"testManyRunsFlowActivity_cluster1"
decl_stmt|;
name|String
name|user
init|=
literal|"testManyRunsFlowActivity_c_user1"
decl_stmt|;
name|String
name|flow
init|=
literal|"flow_activity_test_flow_name"
decl_stmt|;
name|String
name|flowVersion1
init|=
literal|"A122110F135BC4"
decl_stmt|;
name|Long
name|runid1
init|=
literal|11111111111L
decl_stmt|;
name|String
name|flowVersion2
init|=
literal|"A12222222222C4"
decl_stmt|;
name|long
name|runid2
init|=
literal|2222222222222L
decl_stmt|;
name|String
name|flowVersion3
init|=
literal|"A1333333333C4"
decl_stmt|;
name|long
name|runid3
init|=
literal|3333333333333L
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
name|getFlowApp1
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
literal|"application_11888888888_1111"
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
name|flowVersion1
argument_list|,
name|runid1
argument_list|,
name|appName
argument_list|,
name|te
argument_list|)
expr_stmt|;
comment|// write an application with to this flow but a different runid/ version
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
name|entityApp1
argument_list|)
expr_stmt|;
name|appName
operator|=
literal|"application_11888888888_2222"
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
name|flowVersion2
argument_list|,
name|runid2
argument_list|,
name|appName
argument_list|,
name|te
argument_list|)
expr_stmt|;
comment|// write an application with to this flow but a different runid/ version
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
name|entityApp1
argument_list|)
expr_stmt|;
name|appName
operator|=
literal|"application_11888888888_3333"
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
name|flowVersion3
argument_list|,
name|runid3
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
comment|// check flow activity
name|checkFlowActivityTableSeveralRuns
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
argument_list|,
name|c1
argument_list|,
name|flowVersion1
argument_list|,
name|runid1
argument_list|,
name|flowVersion2
argument_list|,
name|runid2
argument_list|,
name|flowVersion3
argument_list|,
name|runid3
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFlowActivityTableSeveralRuns (String cluster, String user, String flow, Configuration c1, String flowVersion1, Long runid1, String flowVersion2, Long runid2, String flowVersion3, Long runid3)
specifier|private
name|void
name|checkFlowActivityTableSeveralRuns
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
name|Configuration
name|c1
parameter_list|,
name|String
name|flowVersion1
parameter_list|,
name|Long
name|runid1
parameter_list|,
name|String
name|flowVersion2
parameter_list|,
name|Long
name|runid2
parameter_list|,
name|String
name|flowVersion3
parameter_list|,
name|Long
name|runid3
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
name|FlowActivityColumnFamily
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
name|FlowActivityRowKey
operator|.
name|getRowKey
argument_list|(
name|cluster
argument_list|,
name|user
argument_list|,
name|flow
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
name|FlowActivityRowKey
operator|.
name|getRowKey
argument_list|(
name|clusterStop
argument_list|,
name|user
argument_list|,
name|flow
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
name|FlowActivityTable
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
name|byte
index|[]
name|row
init|=
name|result
operator|.
name|getRow
argument_list|()
decl_stmt|;
name|FlowActivityRowKey
name|flowActivityRowKey
init|=
name|FlowActivityRowKey
operator|.
name|parseRowKey
argument_list|(
name|row
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|flowActivityRowKey
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cluster
argument_list|,
name|flowActivityRowKey
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|user
argument_list|,
name|flowActivityRowKey
operator|.
name|getUserId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|flow
argument_list|,
name|flowActivityRowKey
operator|.
name|getFlowId
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|dayTs
init|=
name|TimelineWriterUtils
operator|.
name|getTopOfTheDayTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|dayTs
argument_list|,
name|flowActivityRowKey
operator|.
name|getDayTimestamp
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
name|FlowActivityColumnFamily
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
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|values
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkFlowActivityRunId
argument_list|(
name|runid1
argument_list|,
name|flowVersion1
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|checkFlowActivityRunId
argument_list|(
name|runid2
argument_list|,
name|flowVersion2
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|checkFlowActivityRunId
argument_list|(
name|runid3
argument_list|,
name|flowVersion3
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
comment|// the flow activity table is such that it will insert
comment|// into current day's record
comment|// hence, if this test runs across the midnight boundary,
comment|// it may fail since it would insert into two records
comment|// one for each day
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rowCount
argument_list|)
expr_stmt|;
block|}
DECL|method|checkFlowActivityRunId (Long runid, String flowVersion, Map<byte[], byte[]> values)
specifier|private
name|void
name|checkFlowActivityRunId
parameter_list|(
name|Long
name|runid
parameter_list|,
name|String
name|flowVersion
parameter_list|,
name|Map
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|rq
init|=
name|ColumnHelper
operator|.
name|getColumnQualifier
argument_list|(
name|FlowActivityColumnPrefix
operator|.
name|RUN_ID
operator|.
name|getColumnPrefixBytes
argument_list|()
argument_list|,
name|GenericObjectMapper
operator|.
name|write
argument_list|(
name|runid
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|k
range|:
name|values
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|actualQ
init|=
name|Bytes
operator|.
name|toString
argument_list|(
name|k
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Bytes
operator|.
name|toString
argument_list|(
name|rq
argument_list|)
operator|.
name|equals
argument_list|(
name|actualQ
argument_list|)
condition|)
block|{
name|String
name|actualV
init|=
operator|(
name|String
operator|)
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|k
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|flowVersion
argument_list|,
name|actualV
argument_list|)
expr_stmt|;
block|}
block|}
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

