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
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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

begin_class
DECL|class|TestPhoenixTimelineWriterImpl
specifier|public
class|class
name|TestPhoenixTimelineWriterImpl
block|{
DECL|field|writer
specifier|private
name|PhoenixTimelineWriterImpl
name|writer
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// TODO: launch a miniphoenix cluster, or else we're directly operating on
comment|// the active Phoenix cluster
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|writer
operator|=
name|createPhoenixWriter
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
DECL|method|testPhoenixWriterBasic ()
specifier|public
name|void
name|testPhoenixWriterBasic
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Set up a list of timeline entities and write them back to Phoenix
name|int
name|numEntity
init|=
literal|12
decl_stmt|;
name|TimelineEntities
name|te
init|=
name|TestTimelineWriterImpl
operator|.
name|getStandardTestTimelineEntities
argument_list|(
name|numEntity
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"cluster_1"
argument_list|,
literal|"user1"
argument_list|,
literal|"testFlow"
argument_list|,
literal|"version1"
argument_list|,
literal|1l
argument_list|,
literal|"app_test_1"
argument_list|,
name|te
argument_list|)
expr_stmt|;
comment|// Verify if we're storing all entities
name|String
name|sql
init|=
literal|"SELECT COUNT(entity_id) FROM "
operator|+
name|PhoenixTimelineWriterImpl
operator|.
name|ENTITY_TABLE_NAME
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
comment|// Check config (half of all entities)
name|sql
operator|=
literal|"SELECT COUNT(c.config) FROM "
operator|+
name|PhoenixTimelineWriterImpl
operator|.
name|ENTITY_TABLE_NAME
operator|+
literal|"(c.config VARCHAR) "
expr_stmt|;
name|verifySQLWithCount
argument_list|(
name|sql
argument_list|,
operator|(
name|numEntity
operator|/
literal|2
operator|)
argument_list|,
literal|"Number of entities with config should be "
argument_list|)
expr_stmt|;
comment|// Check info (half of all entities)
name|sql
operator|=
literal|"SELECT COUNT(i.info1) FROM "
operator|+
name|PhoenixTimelineWriterImpl
operator|.
name|ENTITY_TABLE_NAME
operator|+
literal|"(i.info1 VARBINARY) "
expr_stmt|;
name|verifySQLWithCount
argument_list|(
name|sql
argument_list|,
operator|(
name|numEntity
operator|/
literal|2
operator|)
argument_list|,
literal|"Number of entities with info should be "
argument_list|)
expr_stmt|;
comment|// Check config and info (a quarter of all entities)
name|sql
operator|=
literal|"SELECT COUNT(entity_id) FROM "
operator|+
name|PhoenixTimelineWriterImpl
operator|.
name|ENTITY_TABLE_NAME
operator|+
literal|"(c.config VARCHAR, i.info1 VARBINARY) "
operator|+
literal|"WHERE c.config IS NOT NULL AND i.info1 IS NOT NULL"
expr_stmt|;
name|verifySQLWithCount
argument_list|(
name|sql
argument_list|,
operator|(
name|numEntity
operator|/
literal|4
operator|)
argument_list|,
literal|"Number of entities with both config and info should be "
argument_list|)
expr_stmt|;
comment|// Check relatesToEntities and isRelatedToEntities
name|sql
operator|=
literal|"SELECT COUNT(entity_id) FROM "
operator|+
name|PhoenixTimelineWriterImpl
operator|.
name|ENTITY_TABLE_NAME
operator|+
literal|"(rt.testType VARCHAR, ir.testType VARCHAR) "
operator|+
literal|"WHERE rt.testType IS NOT NULL AND ir.testType IS NOT NULL"
expr_stmt|;
name|verifySQLWithCount
argument_list|(
name|sql
argument_list|,
name|numEntity
operator|-
literal|2
argument_list|,
literal|"Number of entities with both relatesTo and isRelatedTo should be "
argument_list|)
expr_stmt|;
comment|// Check event
name|sql
operator|=
literal|"SELECT COUNT(entity_id) FROM "
operator|+
name|PhoenixTimelineWriterImpl
operator|.
name|EVENT_TABLE_NAME
expr_stmt|;
name|verifySQLWithCount
argument_list|(
name|sql
argument_list|,
operator|(
name|numEntity
operator|/
literal|4
operator|)
argument_list|,
literal|"Number of events should be "
argument_list|)
expr_stmt|;
comment|// Check metrics
name|sql
operator|=
literal|"SELECT COUNT(entity_id) FROM "
operator|+
name|PhoenixTimelineWriterImpl
operator|.
name|METRIC_TABLE_NAME
expr_stmt|;
name|verifySQLWithCount
argument_list|(
name|sql
argument_list|,
operator|(
name|numEntity
operator|/
literal|4
operator|)
argument_list|,
literal|"Number of events should be "
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Note: it is assumed that we're working on a test only cluster, or else
comment|// this cleanup process will drop the entity table.
name|writer
operator|.
name|dropTable
argument_list|(
name|PhoenixTimelineWriterImpl
operator|.
name|ENTITY_TABLE_NAME
argument_list|)
expr_stmt|;
name|writer
operator|.
name|dropTable
argument_list|(
name|PhoenixTimelineWriterImpl
operator|.
name|EVENT_TABLE_NAME
argument_list|)
expr_stmt|;
name|writer
operator|.
name|dropTable
argument_list|(
name|PhoenixTimelineWriterImpl
operator|.
name|METRIC_TABLE_NAME
argument_list|)
expr_stmt|;
name|writer
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|createPhoenixWriter ( YarnConfiguration conf)
specifier|private
specifier|static
name|PhoenixTimelineWriterImpl
name|createPhoenixWriter
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|PhoenixTimelineWriterImpl
name|myWriter
init|=
operator|new
name|PhoenixTimelineWriterImpl
argument_list|()
decl_stmt|;
name|myWriter
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|myWriter
return|;
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
name|PhoenixTimelineWriterImpl
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

