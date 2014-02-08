begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.apptimeline
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
name|apptimeline
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
name|Arrays
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|apptimeline
operator|.
name|ATSPutErrors
operator|.
name|ATSPutError
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
DECL|class|TestApplicationTimelineRecords
specifier|public
class|class
name|TestApplicationTimelineRecords
block|{
annotation|@
name|Test
DECL|method|testATSEntities ()
specifier|public
name|void
name|testATSEntities
parameter_list|()
block|{
name|ATSEntities
name|entities
init|=
operator|new
name|ATSEntities
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
name|ATSEntity
name|entity
init|=
operator|new
name|ATSEntity
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
name|ATSEvent
name|event
init|=
operator|new
name|ATSEvent
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
name|entities
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
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
name|ATSEntity
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
name|ATSEntity
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
block|}
annotation|@
name|Test
DECL|method|testATSEvents ()
specifier|public
name|void
name|testATSEvents
parameter_list|()
block|{
name|ATSEvents
name|events
init|=
operator|new
name|ATSEvents
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
name|ATSEvents
operator|.
name|ATSEventsOfOneEntity
name|partEvents
init|=
operator|new
name|ATSEvents
operator|.
name|ATSEventsOfOneEntity
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
name|ATSEvent
name|event
init|=
operator|new
name|ATSEvent
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
name|ATSEvents
operator|.
name|ATSEventsOfOneEntity
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
name|ATSEvent
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
name|ATSEvent
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
name|ATSEvents
operator|.
name|ATSEventsOfOneEntity
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
name|ATSEvent
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
name|ATSEvent
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
DECL|method|testATSPutErrors ()
specifier|public
name|void
name|testATSPutErrors
parameter_list|()
block|{
name|ATSPutErrors
name|atsPutErrors
init|=
operator|new
name|ATSPutErrors
argument_list|()
decl_stmt|;
name|ATSPutError
name|error1
init|=
operator|new
name|ATSPutError
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
name|ATSPutError
operator|.
name|NO_START_TIME
argument_list|)
expr_stmt|;
name|atsPutErrors
operator|.
name|addError
argument_list|(
name|error1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ATSPutError
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|ATSPutError
argument_list|>
argument_list|()
decl_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|error1
argument_list|)
expr_stmt|;
name|ATSPutError
name|error2
init|=
operator|new
name|ATSPutError
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
name|ATSPutError
operator|.
name|IO_EXCEPTION
argument_list|)
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|error2
argument_list|)
expr_stmt|;
name|atsPutErrors
operator|.
name|addErrors
argument_list|(
name|errors
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|atsPutErrors
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|ATSPutError
name|e
init|=
name|atsPutErrors
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
name|atsPutErrors
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
name|atsPutErrors
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
block|}
end_class

end_unit

