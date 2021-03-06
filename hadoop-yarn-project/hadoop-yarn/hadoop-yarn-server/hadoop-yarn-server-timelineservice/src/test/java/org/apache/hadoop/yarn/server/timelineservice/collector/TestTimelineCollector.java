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
name|ApplicationId
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
name|TimelineCollector
operator|.
name|AggregationStatusTable
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
name|junit
operator|.
name|Test
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
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|stubbing
operator|.
name|answers
operator|.
name|AnswersWithDelay
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|stubbing
operator|.
name|answers
operator|.
name|Returns
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
import|import static
name|org
operator|.
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
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
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|never
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|TestTimelineCollector
specifier|public
class|class
name|TestTimelineCollector
block|{
DECL|method|generateTestEntities (int groups, int entities)
specifier|private
name|TimelineEntities
name|generateTestEntities
parameter_list|(
name|int
name|groups
parameter_list|,
name|int
name|entities
parameter_list|)
block|{
name|TimelineEntities
name|te
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|groups
condition|;
name|j
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|entities
condition|;
name|i
operator|++
control|)
block|{
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|String
name|containerId
init|=
literal|"container_1000178881110_2002_"
operator|+
name|i
decl_stmt|;
name|entity
operator|.
name|setId
argument_list|(
name|containerId
argument_list|)
expr_stmt|;
name|String
name|entityType
init|=
literal|"TEST_"
operator|+
name|j
decl_stmt|;
name|entity
operator|.
name|setType
argument_list|(
name|entityType
argument_list|)
expr_stmt|;
name|long
name|cTime
init|=
literal|1425016501000L
decl_stmt|;
name|entity
operator|.
name|setCreatedTime
argument_list|(
name|cTime
argument_list|)
expr_stmt|;
comment|// add metrics
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|TimelineMetric
name|m1
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|m1
operator|.
name|setId
argument_list|(
literal|"HDFS_BYTES_WRITE"
argument_list|)
expr_stmt|;
name|m1
operator|.
name|setRealtimeAggregationOp
argument_list|(
name|TimelineMetricOperation
operator|.
name|SUM
argument_list|)
expr_stmt|;
name|long
name|ts
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|m1
operator|.
name|addValue
argument_list|(
name|ts
operator|-
literal|20000
argument_list|,
literal|100L
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|m1
argument_list|)
expr_stmt|;
name|TimelineMetric
name|m2
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|m2
operator|.
name|setId
argument_list|(
literal|"VCORES_USED"
argument_list|)
expr_stmt|;
name|m2
operator|.
name|setRealtimeAggregationOp
argument_list|(
name|TimelineMetricOperation
operator|.
name|SUM
argument_list|)
expr_stmt|;
name|m2
operator|.
name|addValue
argument_list|(
name|ts
operator|-
literal|20000
argument_list|,
literal|3L
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|m2
argument_list|)
expr_stmt|;
comment|// m3 should not show up in the aggregation
name|TimelineMetric
name|m3
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|m3
operator|.
name|setId
argument_list|(
literal|"UNRELATED_VALUES"
argument_list|)
expr_stmt|;
name|m3
operator|.
name|addValue
argument_list|(
name|ts
operator|-
literal|20000
argument_list|,
literal|3L
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|m3
argument_list|)
expr_stmt|;
name|TimelineMetric
name|m4
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|m4
operator|.
name|setId
argument_list|(
literal|"TXN_FINISH_TIME"
argument_list|)
expr_stmt|;
name|m4
operator|.
name|setRealtimeAggregationOp
argument_list|(
name|TimelineMetricOperation
operator|.
name|MAX
argument_list|)
expr_stmt|;
name|m4
operator|.
name|addValue
argument_list|(
name|ts
operator|-
literal|20000
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|add
argument_list|(
name|m4
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addMetrics
argument_list|(
name|metrics
argument_list|)
expr_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|te
return|;
block|}
annotation|@
name|Test
DECL|method|testAggregation ()
specifier|public
name|void
name|testAggregation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Test aggregation with multiple groups.
name|int
name|groups
init|=
literal|3
decl_stmt|;
name|int
name|n
init|=
literal|50
decl_stmt|;
name|TimelineEntities
name|testEntities
init|=
name|generateTestEntities
argument_list|(
name|groups
argument_list|,
name|n
argument_list|)
decl_stmt|;
name|TimelineEntity
name|resultEntity
init|=
name|TimelineCollector
operator|.
name|aggregateEntities
argument_list|(
name|testEntities
argument_list|,
literal|"test_result"
argument_list|,
literal|"TEST_AGGR"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultEntity
operator|.
name|getMetrics
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
name|groups
operator|*
literal|3
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groups
condition|;
name|i
operator|++
control|)
block|{
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
init|=
name|resultEntity
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineMetric
name|m
range|:
name|metrics
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getId
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"HDFS_BYTES_WRITE"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|100
operator|*
name|n
argument_list|,
name|m
operator|.
name|getSingleDataValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|m
operator|.
name|getId
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"VCORES_USED"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|3
operator|*
name|n
argument_list|,
name|m
operator|.
name|getSingleDataValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|m
operator|.
name|getId
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"TXN_FINISH_TIME"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|n
operator|-
literal|1
argument_list|,
name|m
operator|.
name|getSingleDataValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unrecognized metric! "
operator|+
name|m
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Test aggregation with a single group.
name|TimelineEntities
name|testEntities1
init|=
name|generateTestEntities
argument_list|(
literal|1
argument_list|,
name|n
argument_list|)
decl_stmt|;
name|TimelineEntity
name|resultEntity1
init|=
name|TimelineCollector
operator|.
name|aggregateEntities
argument_list|(
name|testEntities1
argument_list|,
literal|"test_result"
argument_list|,
literal|"TEST_AGGR"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|resultEntity1
operator|.
name|getMetrics
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|TimelineMetric
argument_list|>
name|metrics
init|=
name|resultEntity1
operator|.
name|getMetrics
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineMetric
name|m
range|:
name|metrics
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
literal|"HDFS_BYTES_WRITE"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|100
operator|*
name|n
argument_list|,
name|m
operator|.
name|getSingleDataValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|m
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
literal|"VCORES_USED"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|3
operator|*
name|n
argument_list|,
name|m
operator|.
name|getSingleDataValue
argument_list|()
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|m
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
literal|"TXN_FINISH_TIME"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|n
operator|-
literal|1
argument_list|,
name|m
operator|.
name|getSingleDataValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unrecognized metric! "
operator|+
name|m
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test TimelineCollector's interaction with TimelineWriter upon    * putEntity() calls.    */
annotation|@
name|Test
DECL|method|testPutEntity ()
specifier|public
name|void
name|testPutEntity
parameter_list|()
throws|throws
name|IOException
block|{
name|TimelineWriter
name|writer
init|=
name|mock
argument_list|(
name|TimelineWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|TimelineCollector
name|collector
init|=
operator|new
name|TimelineCollectorForTest
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|TimelineEntities
name|entities
init|=
name|generateTestEntities
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|collector
operator|.
name|putEntities
argument_list|(
name|entities
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test-user"
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|writer
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|TimelineCollectorContext
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TimelineEntities
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|writer
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test TimelineCollector's interaction with TimelineWriter upon    * putEntityAsync() calls.    */
annotation|@
name|Test
DECL|method|testPutEntityAsync ()
specifier|public
name|void
name|testPutEntityAsync
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineWriter
name|writer
init|=
name|mock
argument_list|(
name|TimelineWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|TimelineCollector
name|collector
init|=
operator|new
name|TimelineCollectorForTest
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|collector
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|start
argument_list|()
expr_stmt|;
name|TimelineEntities
name|entities
init|=
name|generateTestEntities
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|collector
operator|.
name|putEntitiesAsync
argument_list|(
name|entities
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test-user"
argument_list|)
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|writer
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|TimelineCollectorContext
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TimelineEntities
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|writer
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
name|collector
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test TimelineCollector's discarding entities in case of async writes if    * write is taking too much time.    */
annotation|@
name|Test
DECL|method|testAsyncEntityDiscard ()
specifier|public
name|void
name|testAsyncEntityDiscard
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineWriter
name|writer
init|=
name|mock
argument_list|(
name|TimelineWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|writer
operator|.
name|write
argument_list|(
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|AnswersWithDelay
argument_list|(
literal|500
argument_list|,
operator|new
name|Returns
argument_list|(
operator|new
name|TimelineWriteResponse
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TimelineCollector
name|collector
init|=
operator|new
name|TimelineCollectorForTest
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_WRITER_ASYNC_QUEUE_CAPACITY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|collector
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|collector
operator|.
name|start
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
operator|++
name|i
control|)
block|{
name|TimelineEntities
name|entities
init|=
name|generateTestEntities
argument_list|(
name|i
operator|+
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|collector
operator|.
name|putEntitiesAsync
argument_list|(
name|entities
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"test-user"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|3000
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|writer
argument_list|,
name|times
argument_list|(
literal|4
argument_list|)
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|TimelineCollectorContext
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TimelineEntities
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|writer
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
name|collector
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test TimelineCollector's interaction with TimelineWriter upon    * putDomain() calls.    */
DECL|method|testPutDomain ()
annotation|@
name|Test
specifier|public
name|void
name|testPutDomain
parameter_list|()
throws|throws
name|IOException
block|{
name|TimelineWriter
name|writer
init|=
name|mock
argument_list|(
name|TimelineWriter
operator|.
name|class
argument_list|)
decl_stmt|;
name|TimelineCollector
name|collector
init|=
operator|new
name|TimelineCollectorForTest
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|TimelineDomain
name|domain
init|=
name|generateDomain
argument_list|(
literal|"id"
argument_list|,
literal|"desc"
argument_list|,
literal|"owner"
argument_list|,
literal|"reader1,reader2"
argument_list|,
literal|"writer"
argument_list|,
literal|0L
argument_list|,
literal|1L
argument_list|)
decl_stmt|;
name|collector
operator|.
name|putDomain
argument_list|(
name|domain
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"owner"
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|writer
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|write
argument_list|(
name|any
argument_list|(
name|TimelineCollectorContext
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TimelineDomain
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|writer
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
DECL|method|generateDomain (String id, String desc, String owner, String reader, String writer, Long cTime, Long mTime)
specifier|private
specifier|static
name|TimelineDomain
name|generateDomain
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|desc
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|reader
parameter_list|,
name|String
name|writer
parameter_list|,
name|Long
name|cTime
parameter_list|,
name|Long
name|mTime
parameter_list|)
block|{
name|TimelineDomain
name|domain
init|=
operator|new
name|TimelineDomain
argument_list|()
decl_stmt|;
name|domain
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setDescription
argument_list|(
name|desc
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setOwner
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setReaders
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setWriters
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setCreatedTime
argument_list|(
name|cTime
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setModifiedTime
argument_list|(
name|mTime
argument_list|)
expr_stmt|;
return|return
name|domain
return|;
block|}
DECL|class|TimelineCollectorForTest
specifier|private
specifier|static
class|class
name|TimelineCollectorForTest
extends|extends
name|TimelineCollector
block|{
DECL|field|context
specifier|private
specifier|final
name|TimelineCollectorContext
name|context
init|=
operator|new
name|TimelineCollectorContext
argument_list|()
decl_stmt|;
DECL|method|TimelineCollectorForTest (TimelineWriter writer)
name|TimelineCollectorForTest
parameter_list|(
name|TimelineWriter
name|writer
parameter_list|)
block|{
name|super
argument_list|(
literal|"TimelineCollectorForTest"
argument_list|)
expr_stmt|;
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTimelineEntityContext ()
specifier|public
name|TimelineCollectorContext
name|getTimelineEntityContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
block|}
DECL|method|createEntity (String id, String type)
specifier|private
specifier|static
name|TimelineEntity
name|createEntity
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
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
return|return
name|entity
return|;
block|}
DECL|method|createDummyMetric (long ts, Long value)
specifier|private
specifier|static
name|TimelineMetric
name|createDummyMetric
parameter_list|(
name|long
name|ts
parameter_list|,
name|Long
name|value
parameter_list|)
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
literal|"dummy_metric"
argument_list|)
expr_stmt|;
name|metric
operator|.
name|addValue
argument_list|(
name|ts
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|metric
operator|.
name|setRealtimeAggregationOp
argument_list|(
name|TimelineMetricOperation
operator|.
name|SUM
argument_list|)
expr_stmt|;
return|return
name|metric
return|;
block|}
annotation|@
name|Test
DECL|method|testClearPreviousEntitiesOnAggregation ()
specifier|public
name|void
name|testClearPreviousEntitiesOnAggregation
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|long
name|ts
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|TimelineCollector
name|collector
init|=
operator|new
name|TimelineCollector
argument_list|(
literal|""
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|TimelineCollectorContext
name|getTimelineEntityContext
parameter_list|()
block|{
return|return
operator|new
name|TimelineCollectorContext
argument_list|(
literal|"cluster"
argument_list|,
literal|"user"
argument_list|,
literal|"flow"
argument_list|,
literal|"1"
argument_list|,
literal|1L
argument_list|,
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|ts
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|collector
operator|.
name|init
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|setWriter
argument_list|(
name|mock
argument_list|(
name|TimelineWriter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
comment|// Put 5 entities with different metric values.
name|TimelineEntities
name|entities
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|TimelineEntity
name|entity
init|=
name|createEntity
argument_list|(
literal|"e"
operator|+
name|i
argument_list|,
literal|"type"
argument_list|)
decl_stmt|;
name|entity
operator|.
name|addMetric
argument_list|(
name|createDummyMetric
argument_list|(
name|ts
operator|+
name|i
argument_list|,
name|Long
operator|.
name|valueOf
argument_list|(
name|i
operator|*
literal|50
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|entities
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
name|collector
operator|.
name|putEntities
argument_list|(
name|entities
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineCollectorContext
name|currContext
init|=
name|collector
operator|.
name|getTimelineEntityContext
argument_list|()
decl_stmt|;
comment|// Aggregate the entities.
name|Map
argument_list|<
name|String
argument_list|,
name|AggregationStatusTable
argument_list|>
name|aggregationGroups
init|=
name|collector
operator|.
name|getAggregationGroups
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|aggregationGroups
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEntity
name|aggregatedEntity
init|=
name|TimelineCollector
operator|.
name|aggregateWithoutGroupId
argument_list|(
name|aggregationGroups
argument_list|,
name|currContext
operator|.
name|getAppId
argument_list|()
argument_list|,
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|TimelineMetric
name|aggregatedMetric
init|=
name|aggregatedEntity
operator|.
name|getMetrics
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|750L
argument_list|,
name|aggregatedMetric
operator|.
name|getValues
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimelineMetricOperation
operator|.
name|SUM
argument_list|,
name|aggregatedMetric
operator|.
name|getRealtimeAggregationOp
argument_list|()
argument_list|)
expr_stmt|;
comment|// Aggregate entities.
name|aggregatedEntity
operator|=
name|TimelineCollector
operator|.
name|aggregateWithoutGroupId
argument_list|(
name|aggregationGroups
argument_list|,
name|currContext
operator|.
name|getAppId
argument_list|()
argument_list|,
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|aggregatedMetric
operator|=
name|aggregatedEntity
operator|.
name|getMetrics
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
comment|// No values aggregated as no metrics put for an entity between this
comment|// aggregation and the previous one.
name|assertTrue
argument_list|(
name|aggregatedMetric
operator|.
name|getValues
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimelineMetricOperation
operator|.
name|NOP
argument_list|,
name|aggregatedMetric
operator|.
name|getRealtimeAggregationOp
argument_list|()
argument_list|)
expr_stmt|;
comment|// Put 3 entities.
name|entities
operator|=
operator|new
name|TimelineEntities
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|TimelineEntity
name|entity
init|=
name|createEntity
argument_list|(
literal|"e"
operator|+
name|i
argument_list|,
literal|"type"
argument_list|)
decl_stmt|;
name|entity
operator|.
name|addMetric
argument_list|(
name|createDummyMetric
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|i
argument_list|,
literal|50L
argument_list|)
argument_list|)
expr_stmt|;
name|entities
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
name|aggregationGroups
operator|=
name|collector
operator|.
name|getAggregationGroups
argument_list|()
expr_stmt|;
name|collector
operator|.
name|putEntities
argument_list|(
name|entities
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
expr_stmt|;
comment|// Aggregate entities.
name|aggregatedEntity
operator|=
name|TimelineCollector
operator|.
name|aggregateWithoutGroupId
argument_list|(
name|aggregationGroups
argument_list|,
name|currContext
operator|.
name|getAppId
argument_list|()
argument_list|,
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Last 3 entities picked up for aggregation.
name|aggregatedMetric
operator|=
name|aggregatedEntity
operator|.
name|getMetrics
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|150L
argument_list|,
name|aggregatedMetric
operator|.
name|getValues
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TimelineMetricOperation
operator|.
name|SUM
argument_list|,
name|aggregatedMetric
operator|.
name|getRealtimeAggregationOp
argument_list|()
argument_list|)
expr_stmt|;
name|collector
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

