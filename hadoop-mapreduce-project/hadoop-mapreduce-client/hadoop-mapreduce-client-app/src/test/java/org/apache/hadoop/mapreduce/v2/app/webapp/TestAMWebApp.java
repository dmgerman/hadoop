begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
operator|.
name|AMParams
operator|.
name|APP_ID
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|MockJobs
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
name|yarn
operator|.
name|Clock
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
name|event
operator|.
name|EventHandler
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
name|Apps
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
name|WebApps
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
name|test
operator|.
name|WebAppTests
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
name|inject
operator|.
name|Injector
import|;
end_import

begin_class
DECL|class|TestAMWebApp
specifier|public
class|class
name|TestAMWebApp
block|{
DECL|class|TestAppContext
specifier|static
class|class
name|TestAppContext
implements|implements
name|AppContext
block|{
DECL|field|appAttemptID
specifier|final
name|ApplicationAttemptId
name|appAttemptID
decl_stmt|;
DECL|field|appID
specifier|final
name|ApplicationId
name|appID
decl_stmt|;
DECL|field|user
specifier|final
name|String
name|user
init|=
name|MockJobs
operator|.
name|newUserName
argument_list|()
decl_stmt|;
DECL|field|jobs
specifier|final
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|jobs
decl_stmt|;
DECL|field|startTime
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|TestAppContext (int appid, int numJobs, int numTasks, int numAttempts)
name|TestAppContext
parameter_list|(
name|int
name|appid
parameter_list|,
name|int
name|numJobs
parameter_list|,
name|int
name|numTasks
parameter_list|,
name|int
name|numAttempts
parameter_list|)
block|{
name|appID
operator|=
name|MockJobs
operator|.
name|newAppID
argument_list|(
name|appid
argument_list|)
expr_stmt|;
name|appAttemptID
operator|=
name|MockJobs
operator|.
name|newAppAttemptID
argument_list|(
name|appID
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|jobs
operator|=
name|MockJobs
operator|.
name|newJobs
argument_list|(
name|appID
argument_list|,
name|numJobs
argument_list|,
name|numTasks
argument_list|,
name|numAttempts
argument_list|)
expr_stmt|;
block|}
DECL|method|TestAppContext ()
name|TestAppContext
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
block|{
return|return
name|appAttemptID
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationID ()
specifier|public
name|ApplicationId
name|getApplicationID
parameter_list|()
block|{
return|return
name|appID
return|;
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|CharSequence
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
annotation|@
name|Override
DECL|method|getJob (JobId jobID)
specifier|public
name|Job
name|getJob
parameter_list|(
name|JobId
name|jobID
parameter_list|)
block|{
return|return
name|jobs
operator|.
name|get
argument_list|(
name|jobID
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAllJobs ()
specifier|public
name|Map
argument_list|<
name|JobId
argument_list|,
name|Job
argument_list|>
name|getAllJobs
parameter_list|()
block|{
return|return
name|jobs
return|;
comment|// OK
block|}
annotation|@
name|Override
DECL|method|getEventHandler ()
specifier|public
name|EventHandler
name|getEventHandler
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getClock ()
specifier|public
name|Clock
name|getClock
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationName ()
specifier|public
name|String
name|getApplicationName
parameter_list|()
block|{
return|return
literal|"TestApp"
return|;
block|}
annotation|@
name|Override
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
block|}
DECL|method|testAppControllerIndex ()
annotation|@
name|Test
specifier|public
name|void
name|testAppControllerIndex
parameter_list|()
block|{
name|TestAppContext
name|ctx
init|=
operator|new
name|TestAppContext
argument_list|()
decl_stmt|;
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|createMockInjector
argument_list|(
name|AppContext
operator|.
name|class
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|AppController
name|controller
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|AppController
operator|.
name|class
argument_list|)
decl_stmt|;
name|controller
operator|.
name|index
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|ctx
operator|.
name|appID
operator|.
name|toString
argument_list|()
argument_list|,
name|controller
operator|.
name|get
argument_list|(
name|APP_ID
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAppView ()
annotation|@
name|Test
specifier|public
name|void
name|testAppView
parameter_list|()
block|{
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|AppView
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
operator|new
name|TestAppContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testJobView ()
annotation|@
name|Test
specifier|public
name|void
name|testJobView
parameter_list|()
block|{
name|AppContext
name|appContext
init|=
operator|new
name|TestAppContext
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|getJobParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|JobPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
name|appContext
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|testTasksView ()
annotation|@
name|Test
specifier|public
name|void
name|testTasksView
parameter_list|()
block|{
name|AppContext
name|appContext
init|=
operator|new
name|TestAppContext
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|getTaskParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|TasksPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
name|appContext
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|testTaskView ()
annotation|@
name|Test
specifier|public
name|void
name|testTaskView
parameter_list|()
block|{
name|AppContext
name|appContext
init|=
operator|new
name|TestAppContext
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|getTaskParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|TaskPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
name|appContext
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|getJobParams (AppContext appContext)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getJobParams
parameter_list|(
name|AppContext
name|appContext
parameter_list|)
block|{
name|JobId
name|jobId
init|=
name|appContext
operator|.
name|getAllJobs
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|,
name|MRApps
operator|.
name|toString
argument_list|(
name|jobId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
DECL|method|getTaskParams (AppContext appContext)
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getTaskParams
parameter_list|(
name|AppContext
name|appContext
parameter_list|)
block|{
name|JobId
name|jobId
init|=
name|appContext
operator|.
name|getAllJobs
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Entry
argument_list|<
name|TaskId
argument_list|,
name|Task
argument_list|>
name|e
init|=
name|appContext
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
operator|.
name|getTasks
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getType
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|JOB_ID
argument_list|,
name|MRApps
operator|.
name|toString
argument_list|(
name|jobId
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|TASK_ID
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|AMParams
operator|.
name|TASK_TYPE
argument_list|,
name|MRApps
operator|.
name|taskSymbol
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
DECL|method|testConfView ()
annotation|@
name|Test
specifier|public
name|void
name|testConfView
parameter_list|()
block|{
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|JobConfPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
operator|new
name|TestAppContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|WebApps
operator|.
name|$for
argument_list|(
literal|"yarn"
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
operator|new
name|TestAppContext
argument_list|(
literal|0
argument_list|,
literal|8
argument_list|,
literal|88
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|at
argument_list|(
literal|58888
argument_list|)
operator|.
name|inDevMode
argument_list|()
operator|.
name|start
argument_list|(
operator|new
name|AMWebApp
argument_list|()
argument_list|)
operator|.
name|joinThread
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

