begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|*
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
name|Iterator
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
name|lang
operator|.
name|StringUtils
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
name|mapreduce
operator|.
name|JobACL
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskId
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Task
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRApps
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
name|webapp
operator|.
name|Controller
operator|.
name|RequestContext
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
name|webapp
operator|.
name|MimeType
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
name|webapp
operator|.
name|ResponseInfo
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestAppController
specifier|public
class|class
name|TestAppController
block|{
DECL|field|appController
specifier|private
name|AppControllerForTest
name|appController
decl_stmt|;
DECL|field|ctx
specifier|private
name|RequestContext
name|ctx
decl_stmt|;
DECL|field|job
specifier|private
name|Job
name|job
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|AppContext
name|context
init|=
name|mock
argument_list|(
name|AppContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getApplicationID
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getApplicationName
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"AppName"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"User"
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getStartTime
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|=
name|mock
argument_list|(
name|Job
operator|.
name|class
argument_list|)
expr_stmt|;
name|Task
name|task
init|=
name|mock
argument_list|(
name|Task
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|job
operator|.
name|getTask
argument_list|(
name|any
argument_list|(
name|TaskId
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|task
argument_list|)
expr_stmt|;
name|JobId
name|jobID
init|=
name|MRApps
operator|.
name|toJobID
argument_list|(
literal|"job_01_01"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|context
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|job
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|App
name|app
init|=
operator|new
name|App
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ctx
operator|=
name|mock
argument_list|(
name|RequestContext
operator|.
name|class
argument_list|)
expr_stmt|;
name|appController
operator|=
operator|new
name|AppControllerForTest
argument_list|(
name|app
argument_list|,
name|configuration
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|,
literal|"job_01_01"
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|TASK_ID
argument_list|,
literal|"task_01_01_m01_01"
argument_list|)
expr_stmt|;
block|}
comment|/**    * test bad request should be status 400...    */
annotation|@
name|Test
DECL|method|testBadRequest ()
specifier|public
name|void
name|testBadRequest
parameter_list|()
block|{
name|String
name|message
init|=
literal|"test string"
decl_stmt|;
name|appController
operator|.
name|badRequest
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|verifyExpectations
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBadRequestWithNullMessage ()
specifier|public
name|void
name|testBadRequestWithNullMessage
parameter_list|()
block|{
comment|// It should not throw NullPointerException
name|appController
operator|.
name|badRequest
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|verifyExpectations
argument_list|(
name|StringUtils
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyExpectations (String message)
specifier|private
name|void
name|verifyExpectations
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|verify
argument_list|(
name|ctx
argument_list|)
operator|.
name|setStatus
argument_list|(
literal|400
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application_0_0000"
argument_list|,
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|get
argument_list|(
literal|"app.id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|get
argument_list|(
literal|"rm.web"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bad request: "
operator|+
name|message
argument_list|,
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test the method 'info'.    */
annotation|@
name|Test
DECL|method|testInfo ()
specifier|public
name|void
name|testInfo
parameter_list|()
block|{
name|appController
operator|.
name|info
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|ResponseInfo
operator|.
name|Item
argument_list|>
name|iterator
init|=
name|appController
operator|.
name|getResponseInfo
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|ResponseInfo
operator|.
name|Item
name|item
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Application ID:"
argument_list|,
name|item
operator|.
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"application_0_0000"
argument_list|,
name|item
operator|.
name|value
argument_list|)
expr_stmt|;
name|item
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Application Name:"
argument_list|,
name|item
operator|.
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"AppName"
argument_list|,
name|item
operator|.
name|value
argument_list|)
expr_stmt|;
name|item
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"User:"
argument_list|,
name|item
operator|.
name|key
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"User"
argument_list|,
name|item
operator|.
name|value
argument_list|)
expr_stmt|;
name|item
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Started on:"
argument_list|,
name|item
operator|.
name|key
argument_list|)
expr_stmt|;
name|item
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Elasped: "
argument_list|,
name|item
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Test method 'job'. Should print message about error or set JobPage class for rendering    */
annotation|@
name|Test
DECL|method|testGetJob ()
specifier|public
name|void
name|testGetJob
parameter_list|()
block|{
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|appController
operator|.
name|job
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|appController
operator|.
name|response
argument_list|()
argument_list|)
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Access denied: User user does not have permission to view job job_01_01"
argument_list|,
name|appController
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|remove
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|)
expr_stmt|;
name|appController
operator|.
name|job
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Access denied: User user does not have permission to view job job_01_01Bad Request: Missing job ID"
argument_list|,
name|appController
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|,
literal|"job_01_01"
argument_list|)
expr_stmt|;
name|appController
operator|.
name|job
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|JobPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Test method 'jobCounters'. Should print message about error or set CountersPage class for rendering    */
annotation|@
name|Test
DECL|method|testGetJobCounters ()
specifier|public
name|void
name|testGetJobCounters
parameter_list|()
block|{
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|appController
operator|.
name|jobCounters
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|appController
operator|.
name|response
argument_list|()
argument_list|)
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Access denied: User user does not have permission to view job job_01_01"
argument_list|,
name|appController
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|remove
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|)
expr_stmt|;
name|appController
operator|.
name|jobCounters
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Access denied: User user does not have permission to view job job_01_01Bad Request: Missing job ID"
argument_list|,
name|appController
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|,
literal|"job_01_01"
argument_list|)
expr_stmt|;
name|appController
operator|.
name|jobCounters
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|CountersPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Test method 'taskCounters'. Should print message about error or set CountersPage class for rendering    */
annotation|@
name|Test
DECL|method|testGetTaskCounters ()
specifier|public
name|void
name|testGetTaskCounters
parameter_list|()
block|{
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|appController
operator|.
name|taskCounters
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|appController
operator|.
name|response
argument_list|()
argument_list|)
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Access denied: User user does not have permission to view job job_01_01"
argument_list|,
name|appController
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|remove
argument_list|(
name|AMParams
operator|.
name|TASK_ID
argument_list|)
expr_stmt|;
name|appController
operator|.
name|taskCounters
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Access denied: User user does not have permission to view job job_01_01missing task ID"
argument_list|,
name|appController
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|TASK_ID
argument_list|,
literal|"task_01_01_m01_01"
argument_list|)
expr_stmt|;
name|appController
operator|.
name|taskCounters
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|CountersPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Test method 'singleJobCounter'. Should set SingleCounterPage class for rendering    */
annotation|@
name|Test
DECL|method|testGetSingleJobCounter ()
specifier|public
name|void
name|testGetSingleJobCounter
parameter_list|()
throws|throws
name|IOException
block|{
name|appController
operator|.
name|singleJobCounter
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|SingleCounterPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Test method 'singleTaskCounter'. Should set SingleCounterPage class for rendering    */
annotation|@
name|Test
DECL|method|testGetSingleTaskCounter ()
specifier|public
name|void
name|testGetSingleTaskCounter
parameter_list|()
throws|throws
name|IOException
block|{
name|appController
operator|.
name|singleTaskCounter
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|SingleCounterPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|get
argument_list|(
name|AppController
operator|.
name|COUNTER_GROUP
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|get
argument_list|(
name|AppController
operator|.
name|COUNTER_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Test method 'tasks'. Should set TasksPage class for rendering    */
annotation|@
name|Test
DECL|method|testTasks ()
specifier|public
name|void
name|testTasks
parameter_list|()
block|{
name|appController
operator|.
name|tasks
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|TasksPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Test method 'task'. Should set TaskPage class for rendering and information for title    */
annotation|@
name|Test
DECL|method|testTask ()
specifier|public
name|void
name|testTask
parameter_list|()
block|{
name|appController
operator|.
name|task
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Attempts for task_01_01_m01_01"
argument_list|,
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TaskPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *   Test method 'conf'. Should set JobConfPage class for rendering    */
annotation|@
name|Test
DECL|method|testConfiguration ()
specifier|public
name|void
name|testConfiguration
parameter_list|()
block|{
name|appController
operator|.
name|conf
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|JobConfPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *   Test method 'conf'. Should set AttemptsPage class for rendering or print information about error    */
annotation|@
name|Test
DECL|method|testAttempts ()
specifier|public
name|void
name|testAttempts
parameter_list|()
block|{
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|remove
argument_list|(
name|AMParams
operator|.
name|TASK_TYPE
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|appController
operator|.
name|attempts
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|appController
operator|.
name|response
argument_list|()
argument_list|)
operator|.
name|setContentType
argument_list|(
name|MimeType
operator|.
name|TEXT
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Access denied: User user does not have permission to view job job_01_01"
argument_list|,
name|appController
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|job
operator|.
name|checkAccess
argument_list|(
name|any
argument_list|(
name|UserGroupInformation
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|JobACL
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|remove
argument_list|(
name|AMParams
operator|.
name|TASK_ID
argument_list|)
expr_stmt|;
name|appController
operator|.
name|attempts
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Access denied: User user does not have permission to view job job_01_01"
argument_list|,
name|appController
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|TASK_ID
argument_list|,
literal|"task_01_01_m01_01"
argument_list|)
expr_stmt|;
name|appController
operator|.
name|attempts
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bad request: missing task-type."
argument_list|,
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|TASK_TYPE
argument_list|,
literal|"m"
argument_list|)
expr_stmt|;
name|appController
operator|.
name|attempts
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Bad request: missing attempt-state."
argument_list|,
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|get
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
name|appController
operator|.
name|getProperty
argument_list|()
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|ATTEMPT_STATE
argument_list|,
literal|"State"
argument_list|)
expr_stmt|;
name|appController
operator|.
name|attempts
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|AttemptsPage
operator|.
name|class
argument_list|,
name|appController
operator|.
name|getClazz
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

