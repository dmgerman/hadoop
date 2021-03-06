begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.timelineservice
package|package
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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|ApplicationAttemptId
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
name|ContainerId
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
name|exceptions
operator|.
name|YarnRuntimeException
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
name|util
operator|.
name|timeline
operator|.
name|TimelineUtils
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
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Iterator
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

begin_class
DECL|class|TestTimelineServiceRecords
specifier|public
class|class
name|TestTimelineServiceRecords
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestTimelineServiceRecords
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testTimelineEntities ()
specifier|public
name|void
name|testTimelineEntities
parameter_list|()
throws|throws
name|Exception
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
name|setType
argument_list|(
literal|"test type 1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setId
argument_list|(
literal|"test id 1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addInfo
argument_list|(
literal|"test info key 1"
argument_list|,
literal|"test info value 1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addInfo
argument_list|(
literal|"test info key 2"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"test info value 2"
argument_list|,
literal|"test info value 3"
argument_list|)
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addInfo
argument_list|(
literal|"test info key 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|entity
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"test info key 3"
argument_list|)
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addConfig
argument_list|(
literal|"test config key 1"
argument_list|,
literal|"test config value 1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addConfig
argument_list|(
literal|"test config key 2"
argument_list|,
literal|"test config value 2"
argument_list|)
expr_stmt|;
name|TimelineMetric
name|metric1
init|=
operator|new
name|TimelineMetric
argument_list|(
name|TimelineMetric
operator|.
name|Type
operator|.
name|TIME_SERIES
argument_list|)
decl_stmt|;
name|metric1
operator|.
name|setId
argument_list|(
literal|"test metric id 1"
argument_list|)
expr_stmt|;
name|metric1
operator|.
name|addValue
argument_list|(
literal|1L
argument_list|,
literal|1.0F
argument_list|)
expr_stmt|;
name|metric1
operator|.
name|addValue
argument_list|(
literal|3L
argument_list|,
literal|3.0D
argument_list|)
expr_stmt|;
name|metric1
operator|.
name|addValue
argument_list|(
literal|2L
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TimelineMetric
operator|.
name|Type
operator|.
name|TIME_SERIES
argument_list|,
name|metric1
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Number
argument_list|>
argument_list|>
name|itr
init|=
name|metric1
operator|.
name|getValues
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|Number
argument_list|>
name|entry
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|3L
argument_list|)
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3.0D
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|2L
argument_list|)
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|entry
operator|=
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1.0F
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
name|itr
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addMetric
argument_list|(
name|metric1
argument_list|)
expr_stmt|;
name|TimelineMetric
name|metric2
init|=
operator|new
name|TimelineMetric
argument_list|(
name|TimelineMetric
operator|.
name|Type
operator|.
name|SINGLE_VALUE
argument_list|)
decl_stmt|;
name|metric2
operator|.
name|setId
argument_list|(
literal|"test metric id 1"
argument_list|)
expr_stmt|;
name|metric2
operator|.
name|addValue
argument_list|(
literal|3L
argument_list|,
operator|(
name|short
operator|)
literal|3
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TimelineMetric
operator|.
name|Type
operator|.
name|SINGLE_VALUE
argument_list|,
name|metric2
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|metric2
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
operator|instanceof
name|Short
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|Number
argument_list|>
name|points
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|points
operator|.
name|put
argument_list|(
literal|4L
argument_list|,
literal|4.0D
argument_list|)
expr_stmt|;
name|points
operator|.
name|put
argument_list|(
literal|5L
argument_list|,
literal|5.0D
argument_list|)
expr_stmt|;
try|try
block|{
name|metric2
operator|.
name|setValues
argument_list|(
name|points
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Values cannot contain more than one point in"
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|metric2
operator|.
name|addValues
argument_list|(
name|points
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Values cannot contain more than one point in"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|entity
operator|.
name|addMetric
argument_list|(
name|metric2
argument_list|)
expr_stmt|;
name|TimelineMetric
name|metric3
init|=
operator|new
name|TimelineMetric
argument_list|(
name|TimelineMetric
operator|.
name|Type
operator|.
name|SINGLE_VALUE
argument_list|)
decl_stmt|;
name|metric3
operator|.
name|setId
argument_list|(
literal|"test metric id 1"
argument_list|)
expr_stmt|;
name|metric3
operator|.
name|addValue
argument_list|(
literal|4L
argument_list|,
operator|(
name|short
operator|)
literal|4
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"metric3 should equal to metric2! "
argument_list|,
name|metric3
argument_list|,
name|metric2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"metric1 should not equal to metric2! "
argument_list|,
name|metric1
argument_list|,
name|metric2
argument_list|)
expr_stmt|;
name|TimelineEvent
name|event1
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|event1
operator|.
name|setId
argument_list|(
literal|"test event id 1"
argument_list|)
expr_stmt|;
name|event1
operator|.
name|addInfo
argument_list|(
literal|"test info key 1"
argument_list|,
literal|"test info value 1"
argument_list|)
expr_stmt|;
name|event1
operator|.
name|addInfo
argument_list|(
literal|"test info key 2"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"test info value 2"
argument_list|,
literal|"test info value 3"
argument_list|)
argument_list|)
expr_stmt|;
name|event1
operator|.
name|addInfo
argument_list|(
literal|"test info key 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|event1
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"test info key 3"
argument_list|)
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|event1
operator|.
name|setTimestamp
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addEvent
argument_list|(
name|event1
argument_list|)
expr_stmt|;
name|TimelineEvent
name|event2
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|event2
operator|.
name|setId
argument_list|(
literal|"test event id 2"
argument_list|)
expr_stmt|;
name|event2
operator|.
name|addInfo
argument_list|(
literal|"test info key 1"
argument_list|,
literal|"test info value 1"
argument_list|)
expr_stmt|;
name|event2
operator|.
name|addInfo
argument_list|(
literal|"test info key 2"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"test info value 2"
argument_list|,
literal|"test info value 3"
argument_list|)
argument_list|)
expr_stmt|;
name|event2
operator|.
name|addInfo
argument_list|(
literal|"test info key 3"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|event2
operator|.
name|getInfo
argument_list|()
operator|.
name|get
argument_list|(
literal|"test info key 3"
argument_list|)
operator|instanceof
name|Boolean
argument_list|)
expr_stmt|;
name|event2
operator|.
name|setTimestamp
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addEvent
argument_list|(
name|event2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"event1 should not equal to event2! "
argument_list|,
name|event1
operator|.
name|equals
argument_list|(
name|event2
argument_list|)
argument_list|)
expr_stmt|;
name|TimelineEvent
name|event3
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|event3
operator|.
name|setId
argument_list|(
literal|"test event id 1"
argument_list|)
expr_stmt|;
name|event3
operator|.
name|setTimestamp
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"event1 should equal to event3! "
argument_list|,
name|event3
argument_list|,
name|event1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"event1 should not equal to event2! "
argument_list|,
name|event1
argument_list|,
name|event2
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setCreatedTime
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addRelatesToEntity
argument_list|(
literal|"test type 2"
argument_list|,
literal|"test id 2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addRelatesToEntity
argument_list|(
literal|"test type 3"
argument_list|,
literal|"test id 3"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addIsRelatedToEntity
argument_list|(
literal|"test type 4"
argument_list|,
literal|"test id 4"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addIsRelatedToEntity
argument_list|(
literal|"test type 5"
argument_list|,
literal|"test id 5"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entity
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|TimelineEntities
name|entities
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|TimelineEntity
name|entity1
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entities
operator|.
name|addEntity
argument_list|(
name|entity1
argument_list|)
expr_stmt|;
name|TimelineEntity
name|entity2
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entities
operator|.
name|addEntity
argument_list|(
name|entity2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entities
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"entity 1 should not be valid without type and id"
argument_list|,
name|entity1
operator|.
name|isValid
argument_list|()
argument_list|)
expr_stmt|;
name|entity1
operator|.
name|setId
argument_list|(
literal|"test id 2"
argument_list|)
expr_stmt|;
name|entity1
operator|.
name|setType
argument_list|(
literal|"test type 2"
argument_list|)
expr_stmt|;
name|entity2
operator|.
name|setId
argument_list|(
literal|"test id 1"
argument_list|)
expr_stmt|;
name|entity2
operator|.
name|setType
argument_list|(
literal|"test type 1"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Timeline entity should equal to entity2! "
argument_list|,
name|entity
argument_list|,
name|entity2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotEquals
argument_list|(
literal|"entity1 should not equal to entity! "
argument_list|,
name|entity1
argument_list|,
name|entity
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity should be less than entity1! "
argument_list|,
name|entity1
operator|.
name|compareTo
argument_list|(
name|entity
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity's hash code should be -28727840 but not "
operator|+
name|entity
operator|.
name|hashCode
argument_list|()
argument_list|,
name|entity
operator|.
name|hashCode
argument_list|()
argument_list|,
operator|-
literal|28727840
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFirstClassCitizenEntities ()
specifier|public
name|void
name|testFirstClassCitizenEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|UserEntity
name|user
init|=
operator|new
name|UserEntity
argument_list|()
decl_stmt|;
name|user
operator|.
name|setId
argument_list|(
literal|"test user id"
argument_list|)
expr_stmt|;
name|QueueEntity
name|queue
init|=
operator|new
name|QueueEntity
argument_list|()
decl_stmt|;
name|queue
operator|.
name|setId
argument_list|(
literal|"test queue id"
argument_list|)
expr_stmt|;
name|ClusterEntity
name|cluster
init|=
operator|new
name|ClusterEntity
argument_list|()
decl_stmt|;
name|cluster
operator|.
name|setId
argument_list|(
literal|"test cluster id"
argument_list|)
expr_stmt|;
name|FlowRunEntity
name|flow1
init|=
operator|new
name|FlowRunEntity
argument_list|()
decl_stmt|;
comment|//flow1.setId("test flow id 1");
name|flow1
operator|.
name|setUser
argument_list|(
name|user
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|flow1
operator|.
name|setName
argument_list|(
literal|"test flow name 1"
argument_list|)
expr_stmt|;
name|flow1
operator|.
name|setVersion
argument_list|(
literal|"test flow version 1"
argument_list|)
expr_stmt|;
name|flow1
operator|.
name|setRunId
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|FlowRunEntity
name|flow2
init|=
operator|new
name|FlowRunEntity
argument_list|()
decl_stmt|;
comment|//flow2.setId("test flow run id 2");
name|flow2
operator|.
name|setUser
argument_list|(
name|user
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|flow2
operator|.
name|setName
argument_list|(
literal|"test flow name 2"
argument_list|)
expr_stmt|;
name|flow2
operator|.
name|setVersion
argument_list|(
literal|"test flow version 2"
argument_list|)
expr_stmt|;
name|flow2
operator|.
name|setRunId
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|ApplicationEntity
name|app1
init|=
operator|new
name|ApplicationEntity
argument_list|()
decl_stmt|;
name|app1
operator|.
name|setId
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|app1
operator|.
name|setQueue
argument_list|(
name|queue
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationEntity
name|app2
init|=
operator|new
name|ApplicationEntity
argument_list|()
decl_stmt|;
name|app2
operator|.
name|setId
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|app2
operator|.
name|setQueue
argument_list|(
name|queue
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationAttemptEntity
name|appAttempt
init|=
operator|new
name|ApplicationAttemptEntity
argument_list|()
decl_stmt|;
name|appAttempt
operator|.
name|setId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerEntity
name|container
init|=
operator|new
name|ContainerEntity
argument_list|()
decl_stmt|;
name|container
operator|.
name|setId
argument_list|(
name|ContainerId
operator|.
name|newContainerId
argument_list|(
name|ApplicationAttemptId
operator|.
name|newInstance
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|addChild
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|,
name|flow1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|flow1
operator|.
name|setParent
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_CLUSTER
operator|.
name|toString
argument_list|()
argument_list|,
name|cluster
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|flow1
operator|.
name|addChild
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|,
name|flow2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|flow2
operator|.
name|setParent
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|,
name|flow1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|flow2
operator|.
name|addChild
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|,
name|app1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|flow2
operator|.
name|addChild
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|,
name|app2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|app1
operator|.
name|setParent
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|,
name|flow2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|app1
operator|.
name|addChild
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_APPLICATION_ATTEMPT
operator|.
name|toString
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|appAttempt
operator|.
name|setParent
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_APPLICATION
operator|.
name|toString
argument_list|()
argument_list|,
name|app1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|app2
operator|.
name|setParent
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_FLOW_RUN
operator|.
name|toString
argument_list|()
argument_list|,
name|flow2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|appAttempt
operator|.
name|addChild
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_CONTAINER
operator|.
name|toString
argument_list|()
argument_list|,
name|container
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|container
operator|.
name|setParent
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_APPLICATION_ATTEMPT
operator|.
name|toString
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|cluster
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|flow1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|flow2
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|app1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|app2
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|appAttempt
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|container
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Check parent/children APIs
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|app1
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|flow2
operator|.
name|getType
argument_list|()
argument_list|,
name|app1
operator|.
name|getParent
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|flow2
operator|.
name|getId
argument_list|()
argument_list|,
name|app1
operator|.
name|getParent
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|app1
operator|.
name|addInfo
argument_list|(
name|ApplicationEntity
operator|.
name|PARENT_INFO_KEY
argument_list|,
literal|"invalid parent object"
argument_list|)
expr_stmt|;
try|try
block|{
name|app1
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|YarnRuntimeException
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Parent info is invalid identifier object"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|app1
operator|.
name|getChildren
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|app1
operator|.
name|getChildren
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttempt
operator|.
name|getType
argument_list|()
argument_list|,
name|app1
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|appAttempt
operator|.
name|getId
argument_list|()
argument_list|,
name|app1
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|app1
operator|.
name|addInfo
argument_list|(
name|ApplicationEntity
operator|.
name|CHILDREN_INFO_KEY
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"invalid children set"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|app1
operator|.
name|getChildren
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|YarnRuntimeException
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Children info is invalid identifier set"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|app1
operator|.
name|addInfo
argument_list|(
name|ApplicationEntity
operator|.
name|CHILDREN_INFO_KEY
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"invalid child object"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|app1
operator|.
name|getChildren
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|YarnRuntimeException
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Children info contains invalid identifier object"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUser ()
specifier|public
name|void
name|testUser
parameter_list|()
throws|throws
name|Exception
block|{
name|UserEntity
name|user
init|=
operator|new
name|UserEntity
argument_list|()
decl_stmt|;
name|user
operator|.
name|setId
argument_list|(
literal|"test user id"
argument_list|)
expr_stmt|;
name|user
operator|.
name|addInfo
argument_list|(
literal|"test info key 1"
argument_list|,
literal|"test info value 1"
argument_list|)
expr_stmt|;
name|user
operator|.
name|addInfo
argument_list|(
literal|"test info key 2"
argument_list|,
literal|"test info value 2"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|user
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueue ()
specifier|public
name|void
name|testQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|QueueEntity
name|queue
init|=
operator|new
name|QueueEntity
argument_list|()
decl_stmt|;
name|queue
operator|.
name|setId
argument_list|(
literal|"test queue id"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addInfo
argument_list|(
literal|"test info key 1"
argument_list|,
literal|"test info value 1"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addInfo
argument_list|(
literal|"test info key 2"
argument_list|,
literal|"test info value 2"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|setParent
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_QUEUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test parent queue id"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addChild
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_QUEUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test child queue id 1"
argument_list|)
expr_stmt|;
name|queue
operator|.
name|addChild
argument_list|(
name|TimelineEntityType
operator|.
name|YARN_QUEUE
operator|.
name|toString
argument_list|()
argument_list|,
literal|"test child queue id 2"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|queue
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

