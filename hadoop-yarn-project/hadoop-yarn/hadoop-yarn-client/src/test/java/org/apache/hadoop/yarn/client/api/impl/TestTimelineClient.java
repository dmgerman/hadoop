begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client.api.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|impl
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
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
name|doReturn
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
name|spy
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
name|timeline
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
name|timeline
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
name|timeline
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
name|timeline
operator|.
name|TimelinePutResponse
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
name|client
operator|.
name|api
operator|.
name|TimelineClient
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
name|exceptions
operator|.
name|YarnException
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
name|Test
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_class
DECL|class|TestTimelineClient
specifier|public
class|class
name|TestTimelineClient
block|{
DECL|field|client
specifier|private
name|TimelineClientImpl
name|client
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|client
operator|=
name|spy
argument_list|(
operator|(
name|TimelineClientImpl
operator|)
name|TimelineClient
operator|.
name|createTimelineClient
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPostEntities ()
specifier|public
name|void
name|testPostEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|mockClientResponse
argument_list|(
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|TimelinePutResponse
name|response
init|=
name|client
operator|.
name|putEntities
argument_list|(
name|generateEntity
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPostEntitiesWithError ()
specifier|public
name|void
name|testPostEntitiesWithError
parameter_list|()
throws|throws
name|Exception
block|{
name|mockClientResponse
argument_list|(
name|ClientResponse
operator|.
name|Status
operator|.
name|OK
argument_list|,
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|TimelinePutResponse
name|response
init|=
name|client
operator|.
name|putEntities
argument_list|(
name|generateEntity
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|response
operator|.
name|getErrors
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
literal|"test entity id"
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEntityId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"test entity type"
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getEntityType
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|TimelinePutResponse
operator|.
name|TimelinePutError
operator|.
name|IO_EXCEPTION
argument_list|,
name|response
operator|.
name|getErrors
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getErrorCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is not expected"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testPostEntitiesNoResponse ()
specifier|public
name|void
name|testPostEntitiesNoResponse
parameter_list|()
throws|throws
name|Exception
block|{
name|mockClientResponse
argument_list|(
name|ClientResponse
operator|.
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|putEntities
argument_list|(
name|generateEntity
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Exception is expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
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
literal|"Failed to get the response from the timeline server."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|mockClientResponse (ClientResponse.Status status, boolean hasError)
specifier|private
name|ClientResponse
name|mockClientResponse
parameter_list|(
name|ClientResponse
operator|.
name|Status
name|status
parameter_list|,
name|boolean
name|hasError
parameter_list|)
block|{
name|ClientResponse
name|response
init|=
name|mock
argument_list|(
name|ClientResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|response
argument_list|)
operator|.
name|when
argument_list|(
name|client
argument_list|)
operator|.
name|doPostingEntities
argument_list|(
name|any
argument_list|(
name|TimelineEntities
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|response
operator|.
name|getClientResponseStatus
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|TimelinePutResponse
operator|.
name|TimelinePutError
name|error
init|=
operator|new
name|TimelinePutResponse
operator|.
name|TimelinePutError
argument_list|()
decl_stmt|;
name|error
operator|.
name|setEntityId
argument_list|(
literal|"test entity id"
argument_list|)
expr_stmt|;
name|error
operator|.
name|setEntityType
argument_list|(
literal|"test entity type"
argument_list|)
expr_stmt|;
name|error
operator|.
name|setErrorCode
argument_list|(
name|TimelinePutResponse
operator|.
name|TimelinePutError
operator|.
name|IO_EXCEPTION
argument_list|)
expr_stmt|;
name|TimelinePutResponse
name|putResponse
init|=
operator|new
name|TimelinePutResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasError
condition|)
block|{
name|putResponse
operator|.
name|addError
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
name|when
argument_list|(
name|response
operator|.
name|getEntity
argument_list|(
name|TimelinePutResponse
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|putResponse
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|generateEntity ()
specifier|private
specifier|static
name|TimelineEntity
name|generateEntity
parameter_list|()
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
literal|"entity id"
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setEntityType
argument_list|(
literal|"entity type"
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
literal|"test event type "
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
return|return
name|entity
return|;
block|}
block|}
end_class

end_unit

