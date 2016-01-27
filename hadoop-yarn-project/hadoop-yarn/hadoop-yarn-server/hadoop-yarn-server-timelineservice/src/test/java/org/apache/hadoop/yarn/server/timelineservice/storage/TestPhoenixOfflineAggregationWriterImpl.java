begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|IntegrationTestingUtility
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
name|conf
operator|.
name|YarnConfiguration
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
name|collector
operator|.
name|TimelineCollectorContext
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
name|OfflineAggregationInfo
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|phoenix
operator|.
name|hbase
operator|.
name|index
operator|.
name|write
operator|.
name|IndexWriterUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|phoenix
operator|.
name|query
operator|.
name|BaseTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|phoenix
operator|.
name|query
operator|.
name|QueryServices
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|phoenix
operator|.
name|util
operator|.
name|ReadOnlyProps
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|phoenix
operator|.
name|util
operator|.
name|TestUtil
operator|.
name|TEST_PROPERTIES
import|;
end_import

begin_class
DECL|class|TestPhoenixOfflineAggregationWriterImpl
specifier|public
class|class
name|TestPhoenixOfflineAggregationWriterImpl
extends|extends
name|BaseTest
block|{
DECL|field|storage
specifier|private
specifier|static
name|PhoenixOfflineAggregationWriterImpl
name|storage
decl_stmt|;
DECL|field|BATCH_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BATCH_SIZE
init|=
literal|3
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|storage
operator|=
name|setupPhoenixClusterAndWriterForTest
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testFlowLevelAggregationStorage ()
specifier|public
name|void
name|testFlowLevelAggregationStorage
parameter_list|()
throws|throws
name|Exception
block|{
name|testAggregator
argument_list|(
name|OfflineAggregationInfo
operator|.
name|FLOW_AGGREGATION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|90000
argument_list|)
DECL|method|testUserLevelAggregationStorage ()
specifier|public
name|void
name|testUserLevelAggregationStorage
parameter_list|()
throws|throws
name|Exception
block|{
name|testAggregator
argument_list|(
name|OfflineAggregationInfo
operator|.
name|USER_AGGREGATION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanup ()
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
name|storage
operator|.
name|dropTable
argument_list|(
name|OfflineAggregationInfo
operator|.
name|FLOW_AGGREGATION_TABLE_NAME
argument_list|)
expr_stmt|;
name|storage
operator|.
name|dropTable
argument_list|(
name|OfflineAggregationInfo
operator|.
name|USER_AGGREGATION_TABLE_NAME
argument_list|)
expr_stmt|;
name|tearDownMiniCluster
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|PhoenixOfflineAggregationWriterImpl
DECL|method|setupPhoenixClusterAndWriterForTest (YarnConfiguration conf)
name|setupPhoenixClusterAndWriterForTest
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Must update config before starting server
name|props
operator|.
name|put
argument_list|(
name|QueryServices
operator|.
name|STATS_USE_CURRENT_TIME_ATTRIB
argument_list|,
name|Boolean
operator|.
name|FALSE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"java.security.krb5.realm"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"java.security.krb5.kdc"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|IntegrationTestingUtility
operator|.
name|IS_DISTRIBUTED_CLUSTER
argument_list|,
name|Boolean
operator|.
name|FALSE
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|QueryServices
operator|.
name|QUEUE_SIZE_ATTRIB
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|5000
argument_list|)
argument_list|)
expr_stmt|;
name|props
operator|.
name|put
argument_list|(
name|IndexWriterUtils
operator|.
name|HTABLE_THREAD_KEY
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// Make a small batch size to test multiple calls to reserve sequences
name|props
operator|.
name|put
argument_list|(
name|QueryServices
operator|.
name|SEQUENCE_CACHE_SIZE_ATTRIB
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|BATCH_SIZE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Must update config before starting server
name|setUpTestDriver
argument_list|(
operator|new
name|ReadOnlyProps
argument_list|(
name|props
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Change connection settings for test
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|PHOENIX_OFFLINE_STORAGE_CONN_STR
argument_list|,
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
name|PhoenixOfflineAggregationWriterImpl
name|myWriter
init|=
operator|new
name|PhoenixOfflineAggregationWriterImpl
argument_list|(
name|TEST_PROPERTIES
argument_list|)
decl_stmt|;
name|myWriter
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|myWriter
operator|.
name|start
argument_list|()
expr_stmt|;
name|myWriter
operator|.
name|createPhoenixTables
argument_list|()
expr_stmt|;
return|return
name|myWriter
return|;
block|}
DECL|method|getTestAggregationTimelineEntity ()
specifier|private
specifier|static
name|TimelineEntity
name|getTestAggregationTimelineEntity
parameter_list|()
block|{
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|String
name|id
init|=
literal|"hello1"
decl_stmt|;
name|String
name|type
init|=
literal|"testAggregationType"
decl_stmt|;
name|entity
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setCreatedTime
argument_list|(
literal|1425016501000L
argument_list|)
expr_stmt|;
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
literal|"HDFS_BYTES_READ"
argument_list|)
expr_stmt|;
name|metric
operator|.
name|addValue
argument_list|(
literal|1425016501100L
argument_list|,
literal|8000
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addMetric
argument_list|(
name|metric
argument_list|)
expr_stmt|;
return|return
name|entity
return|;
block|}
DECL|method|testAggregator (OfflineAggregationInfo aggregationInfo)
specifier|private
name|void
name|testAggregator
parameter_list|(
name|OfflineAggregationInfo
name|aggregationInfo
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Set up a list of timeline entities and write them back to Phoenix
name|int
name|numEntity
init|=
literal|1
decl_stmt|;
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
name|getTestAggregationTimelineEntity
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineCollectorContext
name|context
init|=
operator|new
name|TimelineCollectorContext
argument_list|(
literal|"cluster_1"
argument_list|,
literal|"user1"
argument_list|,
literal|"testFlow"
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|storage
operator|.
name|writeAggregatedEntity
argument_list|(
name|context
argument_list|,
name|te
argument_list|,
name|aggregationInfo
argument_list|)
expr_stmt|;
comment|// Verify if we're storing all entities
name|String
index|[]
name|primaryKeyList
init|=
name|aggregationInfo
operator|.
name|getPrimaryKeyList
argument_list|()
decl_stmt|;
name|String
name|sql
init|=
literal|"SELECT COUNT("
operator|+
name|primaryKeyList
index|[
name|primaryKeyList
operator|.
name|length
operator|-
literal|1
index|]
operator|+
literal|") FROM "
operator|+
name|aggregationInfo
operator|.
name|getTableName
argument_list|()
decl_stmt|;
name|verifySQLWithCount
argument_list|(
name|sql
argument_list|,
name|numEntity
argument_list|,
literal|"Number of entities should be "
argument_list|)
expr_stmt|;
comment|// Check metric
name|sql
operator|=
literal|"SELECT COUNT(m.HDFS_BYTES_READ) FROM "
operator|+
name|aggregationInfo
operator|.
name|getTableName
argument_list|()
operator|+
literal|"(m.HDFS_BYTES_READ VARBINARY) "
expr_stmt|;
name|verifySQLWithCount
argument_list|(
name|sql
argument_list|,
name|numEntity
argument_list|,
literal|"Number of entities with info should be "
argument_list|)
expr_stmt|;
block|}
DECL|method|verifySQLWithCount (String sql, int targetCount, String message)
specifier|private
name|void
name|verifySQLWithCount
parameter_list|(
name|String
name|sql
parameter_list|,
name|int
name|targetCount
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|Statement
name|stmt
init|=
name|storage
operator|.
name|getConnection
argument_list|()
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
init|)
block|{
name|assertTrue
argument_list|(
literal|"Result set empty on statement "
operator|+
name|sql
argument_list|,
name|rs
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Fail to execute query "
operator|+
name|sql
argument_list|,
name|rs
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|message
operator|+
literal|" "
operator|+
name|targetCount
argument_list|,
name|targetCount
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
name|fail
argument_list|(
literal|"SQL exception on query: "
operator|+
name|sql
operator|+
literal|" With exception message: "
operator|+
name|se
operator|.
name|getLocalizedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

