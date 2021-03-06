begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.timeline
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
name|timeline
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import

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
name|timeline
operator|.
name|TimelinePutResponse
operator|.
name|TimelinePutError
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
name|Assert
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

begin_class
DECL|class|TestTimelineRecords
specifier|public
class|class
name|TestTimelineRecords
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
name|TestTimelineRecords
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testEntities ()
specifier|public
name|void
name|testEntities
parameter_list|()
throws|throws
name|Exception
block|{
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
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|2
condition|;
operator|++
name|j
control|)
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
name|setEntityId
argument_list|(
literal|"entity id "
operator|+
name|j
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setEntityType
argument_list|(
literal|"entity type "
operator|+
name|j
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setStartTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|TimelineEvent
name|event
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|event
operator|.
name|setTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|event
operator|.
name|setEventType
argument_list|(
literal|"event type "
operator|+
name|i
argument_list|)
expr_stmt|;
name|event
operator|.
name|addEventInfo
argument_list|(
literal|"key1"
argument_list|,
literal|"val1"
argument_list|)
expr_stmt|;
name|event
operator|.
name|addEventInfo
argument_list|(
literal|"key2"
argument_list|,
literal|"val2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
name|entity
operator|.
name|addRelatedEntity
argument_list|(
literal|"test ref type 1"
argument_list|,
literal|"test ref id 1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addRelatedEntity
argument_list|(
literal|"test ref type 2"
argument_list|,
literal|"test ref id 2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addPrimaryFilter
argument_list|(
literal|"pkey1"
argument_list|,
literal|"pval1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addPrimaryFilter
argument_list|(
literal|"pkey2"
argument_list|,
literal|"pval2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addOtherInfo
argument_list|(
literal|"okey1"
argument_list|,
literal|"oval1"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addOtherInfo
argument_list|(
literal|"okey2"
argument_list|,
literal|"oval2"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setDomainId
argument_list|(
literal|"domain id "
operator|+
name|j
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Entities in JSON:"
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
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|entities
operator|.
name|getEntities
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEntity
name|entity1
init|=
name|entities
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity id 0"
argument_list|,
name|entity1
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity type 0"
argument_list|,
name|entity1
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|entity1
operator|.
name|getRelatedEntities
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
literal|2
argument_list|,
name|entity1
operator|.
name|getEvents
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
literal|2
argument_list|,
name|entity1
operator|.
name|getPrimaryFilters
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
literal|2
argument_list|,
name|entity1
operator|.
name|getOtherInfo
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
literal|"domain id 0"
argument_list|,
name|entity1
operator|.
name|getDomainId
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEntity
name|entity2
init|=
name|entities
operator|.
name|getEntities
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity id 1"
argument_list|,
name|entity2
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity type 1"
argument_list|,
name|entity2
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|entity2
operator|.
name|getRelatedEntities
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
literal|2
argument_list|,
name|entity2
operator|.
name|getEvents
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
literal|2
argument_list|,
name|entity2
operator|.
name|getPrimaryFilters
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
literal|2
argument_list|,
name|entity2
operator|.
name|getOtherInfo
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
literal|"domain id 1"
argument_list|,
name|entity2
operator|.
name|getDomainId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEvents ()
specifier|public
name|void
name|testEvents
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineEvents
name|events
init|=
operator|new
name|TimelineEvents
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
literal|2
condition|;
operator|++
name|j
control|)
block|{
name|TimelineEvents
operator|.
name|EventsOfOneEntity
name|partEvents
init|=
operator|new
name|TimelineEvents
operator|.
name|EventsOfOneEntity
argument_list|()
decl_stmt|;
name|partEvents
operator|.
name|setEntityId
argument_list|(
literal|"entity id "
operator|+
name|j
argument_list|)
expr_stmt|;
name|partEvents
operator|.
name|setEntityType
argument_list|(
literal|"entity type "
operator|+
name|j
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
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|TimelineEvent
name|event
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|event
operator|.
name|setTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|event
operator|.
name|setEventType
argument_list|(
literal|"event type "
operator|+
name|i
argument_list|)
expr_stmt|;
name|event
operator|.
name|addEventInfo
argument_list|(
literal|"key1"
argument_list|,
literal|"val1"
argument_list|)
expr_stmt|;
name|event
operator|.
name|addEventInfo
argument_list|(
literal|"key2"
argument_list|,
literal|"val2"
argument_list|)
expr_stmt|;
name|partEvents
operator|.
name|addEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
name|events
operator|.
name|addEvent
argument_list|(
name|partEvents
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Events in JSON:"
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
name|events
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|events
operator|.
name|getAllEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEvents
operator|.
name|EventsOfOneEntity
name|partEvents1
init|=
name|events
operator|.
name|getAllEvents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity id 0"
argument_list|,
name|partEvents1
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity type 0"
argument_list|,
name|partEvents1
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|partEvents1
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEvent
name|event11
init|=
name|partEvents1
operator|.
name|getEvents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"event type 0"
argument_list|,
name|event11
operator|.
name|getEventType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|event11
operator|.
name|getEventInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEvent
name|event12
init|=
name|partEvents1
operator|.
name|getEvents
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"event type 1"
argument_list|,
name|event12
operator|.
name|getEventType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|event12
operator|.
name|getEventInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEvents
operator|.
name|EventsOfOneEntity
name|partEvents2
init|=
name|events
operator|.
name|getAllEvents
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity id 1"
argument_list|,
name|partEvents2
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"entity type 1"
argument_list|,
name|partEvents2
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|partEvents2
operator|.
name|getEvents
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEvent
name|event21
init|=
name|partEvents2
operator|.
name|getEvents
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"event type 0"
argument_list|,
name|event21
operator|.
name|getEventType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|event21
operator|.
name|getEventInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelineEvent
name|event22
init|=
name|partEvents2
operator|.
name|getEvents
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"event type 1"
argument_list|,
name|event22
operator|.
name|getEventType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|event22
operator|.
name|getEventInfo
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimelinePutErrors ()
specifier|public
name|void
name|testTimelinePutErrors
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelinePutResponse
name|TimelinePutErrors
init|=
operator|new
name|TimelinePutResponse
argument_list|()
decl_stmt|;
name|TimelinePutError
name|error1
init|=
operator|new
name|TimelinePutError
argument_list|()
decl_stmt|;
name|error1
operator|.
name|setEntityId
argument_list|(
literal|"entity id 1"
argument_list|)
expr_stmt|;
name|error1
operator|.
name|setEntityId
argument_list|(
literal|"entity type 1"
argument_list|)
expr_stmt|;
name|error1
operator|.
name|setErrorCode
argument_list|(
name|TimelinePutError
operator|.
name|NO_START_TIME
argument_list|)
expr_stmt|;
name|TimelinePutErrors
operator|.
name|addError
argument_list|(
name|error1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TimelinePutError
argument_list|>
name|response
init|=
operator|new
name|ArrayList
argument_list|<
name|TimelinePutError
argument_list|>
argument_list|()
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
name|error1
argument_list|)
expr_stmt|;
name|TimelinePutError
name|error2
init|=
operator|new
name|TimelinePutError
argument_list|()
decl_stmt|;
name|error2
operator|.
name|setEntityId
argument_list|(
literal|"entity id 2"
argument_list|)
expr_stmt|;
name|error2
operator|.
name|setEntityId
argument_list|(
literal|"entity type 2"
argument_list|)
expr_stmt|;
name|error2
operator|.
name|setErrorCode
argument_list|(
name|TimelinePutError
operator|.
name|IO_EXCEPTION
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
name|error2
argument_list|)
expr_stmt|;
name|TimelinePutErrors
operator|.
name|addErrors
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Errors in JSON:"
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
name|TimelinePutErrors
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|TimelinePutErrors
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|TimelinePutError
name|e
init|=
name|TimelinePutErrors
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error1
operator|.
name|getEntityId
argument_list|()
argument_list|,
name|e
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error1
operator|.
name|getEntityType
argument_list|()
argument_list|,
name|e
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error1
operator|.
name|getErrorCode
argument_list|()
argument_list|,
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|=
name|TimelinePutErrors
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error1
operator|.
name|getEntityId
argument_list|()
argument_list|,
name|e
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error1
operator|.
name|getEntityType
argument_list|()
argument_list|,
name|e
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error1
operator|.
name|getErrorCode
argument_list|()
argument_list|,
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|=
name|TimelinePutErrors
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error2
operator|.
name|getEntityId
argument_list|()
argument_list|,
name|e
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error2
operator|.
name|getEntityType
argument_list|()
argument_list|,
name|e
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|error2
operator|.
name|getErrorCode
argument_list|()
argument_list|,
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimelineDomain ()
specifier|public
name|void
name|testTimelineDomain
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineDomains
name|domains
init|=
operator|new
name|TimelineDomains
argument_list|()
decl_stmt|;
name|TimelineDomain
name|domain
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|domain
operator|=
operator|new
name|TimelineDomain
argument_list|()
expr_stmt|;
name|domain
operator|.
name|setId
argument_list|(
literal|"test id "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setDescription
argument_list|(
literal|"test description "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setOwner
argument_list|(
literal|"test owner "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setReaders
argument_list|(
literal|"test_reader_user_"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" test_reader_group+"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setWriters
argument_list|(
literal|"test_writer_user_"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" test_writer_group+"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setCreatedTime
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|domain
operator|.
name|setModifiedTime
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|domains
operator|.
name|addDomain
argument_list|(
name|domain
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Domain in JSON:"
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
name|domains
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|domains
operator|.
name|getDomains
argument_list|()
operator|.
name|size
argument_list|()
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
name|domains
operator|.
name|getDomains
argument_list|()
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|domain
operator|=
name|domains
operator|.
name|getDomains
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test id "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|domain
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test description "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|domain
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test owner "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|domain
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test_reader_user_"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" test_reader_group+"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|domain
operator|.
name|getReaders
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test_writer_user_"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|" test_writer_group+"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|,
name|domain
operator|.
name|getWriters
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
operator|new
name|Long
argument_list|(
literal|0L
argument_list|)
argument_list|,
name|domain
operator|.
name|getCreatedTime
argument_list|()
argument_list|)
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
name|domain
operator|.
name|getModifiedTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testMapInterfaceOrTimelineRecords ()
specifier|public
name|void
name|testMapInterfaceOrTimelineRecords
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
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
name|primaryFiltersList
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|primaryFiltersList
operator|.
name|add
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"pkey"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
operator|(
name|Object
operator|)
literal|"pval"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|primaryFilters
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|primaryFilters
operator|.
name|put
argument_list|(
literal|"pkey1"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
operator|(
name|Object
operator|)
literal|"pval1"
argument_list|)
argument_list|)
expr_stmt|;
name|primaryFilters
operator|.
name|put
argument_list|(
literal|"pkey2"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
operator|(
name|Object
operator|)
literal|"pval2"
argument_list|)
argument_list|)
expr_stmt|;
name|primaryFiltersList
operator|.
name|add
argument_list|(
name|primaryFilters
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setPrimaryFilters
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|primaryFiltersToSet
range|:
name|primaryFiltersList
control|)
block|{
name|entity
operator|.
name|setPrimaryFilters
argument_list|(
name|primaryFiltersToSet
argument_list|)
expr_stmt|;
name|assertPrimaryFilters
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
name|primaryFiltersToAdd
init|=
operator|new
name|WeakHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|primaryFiltersToAdd
operator|.
name|put
argument_list|(
literal|"pkey3"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
operator|(
name|Object
operator|)
literal|"pval3"
argument_list|)
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addPrimaryFilters
argument_list|(
name|primaryFiltersToAdd
argument_list|)
expr_stmt|;
name|assertPrimaryFilters
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
name|relatedEntitiesList
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|relatedEntitiesList
operator|.
name|add
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"rkey"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"rval"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatedEntities
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|relatedEntities
operator|.
name|put
argument_list|(
literal|"rkey1"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"rval1"
argument_list|)
argument_list|)
expr_stmt|;
name|relatedEntities
operator|.
name|put
argument_list|(
literal|"rkey2"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"rval2"
argument_list|)
argument_list|)
expr_stmt|;
name|relatedEntitiesList
operator|.
name|add
argument_list|(
name|relatedEntities
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setRelatedEntities
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatedEntitiesToSet
range|:
name|relatedEntitiesList
control|)
block|{
name|entity
operator|.
name|setRelatedEntities
argument_list|(
name|relatedEntitiesToSet
argument_list|)
expr_stmt|;
name|assertRelatedEntities
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|relatedEntitiesToAdd
init|=
operator|new
name|WeakHashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|relatedEntitiesToAdd
operator|.
name|put
argument_list|(
literal|"rkey3"
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"rval3"
argument_list|)
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addRelatedEntities
argument_list|(
name|relatedEntitiesToAdd
argument_list|)
expr_stmt|;
name|assertRelatedEntities
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|otherInfoList
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|otherInfoList
operator|.
name|add
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"okey"
argument_list|,
operator|(
name|Object
operator|)
literal|"oval"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherInfo
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|otherInfo
operator|.
name|put
argument_list|(
literal|"okey1"
argument_list|,
literal|"oval1"
argument_list|)
expr_stmt|;
name|otherInfo
operator|.
name|put
argument_list|(
literal|"okey2"
argument_list|,
literal|"oval2"
argument_list|)
expr_stmt|;
name|otherInfoList
operator|.
name|add
argument_list|(
name|otherInfo
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setOtherInfo
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherInfoToSet
range|:
name|otherInfoList
control|)
block|{
name|entity
operator|.
name|setOtherInfo
argument_list|(
name|otherInfoToSet
argument_list|)
expr_stmt|;
name|assertOtherInfo
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|otherInfoToAdd
init|=
operator|new
name|WeakHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|otherInfoToAdd
operator|.
name|put
argument_list|(
literal|"okey3"
argument_list|,
literal|"oval3"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|addOtherInfo
argument_list|(
name|otherInfoToAdd
argument_list|)
expr_stmt|;
name|assertOtherInfo
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
name|TimelineEvent
name|event
init|=
operator|new
name|TimelineEvent
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|eventInfoList
init|=
operator|new
name|ArrayList
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|eventInfoList
operator|.
name|add
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"ekey"
argument_list|,
operator|(
name|Object
operator|)
literal|"eval"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|eventInfo
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
literal|"ekey1"
argument_list|,
literal|"eval1"
argument_list|)
expr_stmt|;
name|eventInfo
operator|.
name|put
argument_list|(
literal|"ekey2"
argument_list|,
literal|"eval2"
argument_list|)
expr_stmt|;
name|eventInfoList
operator|.
name|add
argument_list|(
name|eventInfo
argument_list|)
expr_stmt|;
name|event
operator|.
name|setEventInfo
argument_list|(
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|eventInfoToSet
range|:
name|eventInfoList
control|)
block|{
name|event
operator|.
name|setEventInfo
argument_list|(
name|eventInfoToSet
argument_list|)
expr_stmt|;
name|assertEventInfo
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|eventInfoToAdd
init|=
operator|new
name|WeakHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|eventInfoToAdd
operator|.
name|put
argument_list|(
literal|"ekey3"
argument_list|,
literal|"eval3"
argument_list|)
expr_stmt|;
name|event
operator|.
name|addEventInfo
argument_list|(
name|eventInfoToAdd
argument_list|)
expr_stmt|;
name|assertEventInfo
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertPrimaryFilters (TimelineEntity entity)
specifier|private
specifier|static
name|void
name|assertPrimaryFilters
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|entity
operator|.
name|getPrimaryFilters
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|entity
operator|.
name|getPrimaryFiltersJAXB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|entity
operator|.
name|getPrimaryFilters
argument_list|()
operator|instanceof
name|HashMap
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|entity
operator|.
name|getPrimaryFiltersJAXB
argument_list|()
operator|instanceof
name|HashMap
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|entity
operator|.
name|getPrimaryFilters
argument_list|()
argument_list|,
name|entity
operator|.
name|getPrimaryFiltersJAXB
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertRelatedEntities (TimelineEntity entity)
specifier|private
specifier|static
name|void
name|assertRelatedEntities
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|entity
operator|.
name|getRelatedEntities
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|entity
operator|.
name|getRelatedEntitiesJAXB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|entity
operator|.
name|getRelatedEntities
argument_list|()
operator|instanceof
name|HashMap
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|entity
operator|.
name|getRelatedEntitiesJAXB
argument_list|()
operator|instanceof
name|HashMap
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|entity
operator|.
name|getRelatedEntities
argument_list|()
argument_list|,
name|entity
operator|.
name|getRelatedEntitiesJAXB
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertOtherInfo (TimelineEntity entity)
specifier|private
specifier|static
name|void
name|assertOtherInfo
parameter_list|(
name|TimelineEntity
name|entity
parameter_list|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|entity
operator|.
name|getOtherInfo
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|entity
operator|.
name|getOtherInfoJAXB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|entity
operator|.
name|getOtherInfo
argument_list|()
operator|instanceof
name|HashMap
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|entity
operator|.
name|getOtherInfoJAXB
argument_list|()
operator|instanceof
name|HashMap
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|entity
operator|.
name|getOtherInfo
argument_list|()
argument_list|,
name|entity
operator|.
name|getOtherInfoJAXB
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertEventInfo (TimelineEvent event)
specifier|private
specifier|static
name|void
name|assertEventInfo
parameter_list|(
name|TimelineEvent
name|event
parameter_list|)
block|{
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|event
operator|.
name|getEventInfoJAXB
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|event
operator|.
name|getEventInfo
argument_list|()
operator|instanceof
name|HashMap
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|event
operator|.
name|getEventInfoJAXB
argument_list|()
operator|instanceof
name|HashMap
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|event
operator|.
name|getEventInfo
argument_list|()
argument_list|,
name|event
operator|.
name|getEventInfoJAXB
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

