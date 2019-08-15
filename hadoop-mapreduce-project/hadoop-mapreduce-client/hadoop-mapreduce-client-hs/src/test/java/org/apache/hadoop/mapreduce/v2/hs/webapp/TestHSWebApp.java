begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.webapp
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
name|hs
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
name|ATTEMPT_STATE
import|;
end_import

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
name|JOB_ID
import|;
end_import

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
name|TASK_TYPE
import|;
end_import

begin_import
import|import static
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
name|YarnWebParams
operator|.
name|APP_OWNER
import|;
end_import

begin_import
import|import static
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
name|YarnWebParams
operator|.
name|CONTAINER_ID
import|;
end_import

begin_import
import|import static
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
name|YarnWebParams
operator|.
name|CONTAINER_LOG_TYPE
import|;
end_import

begin_import
import|import static
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
name|YarnWebParams
operator|.
name|ENTITY_STRING
import|;
end_import

begin_import
import|import static
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
name|YarnWebParams
operator|.
name|NM_NODENAME
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
name|mockito
operator|.
name|Mockito
operator|.
name|verify
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
name|io
operator|.
name|PrintWriter
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
name|MRApp
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
name|MockAppContext
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
name|webapp
operator|.
name|TestAMWebApp
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
name|NodeId
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
name|webapp
operator|.
name|View
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
name|log
operator|.
name|AggregatedLogsPage
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|AbstractModule
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

begin_class
DECL|class|TestHSWebApp
specifier|public
class|class
name|TestHSWebApp
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
name|TestHSWebApp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|testAppControllerIndex ()
annotation|@
name|Test
specifier|public
name|void
name|testAppControllerIndex
parameter_list|()
block|{
name|MockAppContext
name|ctx
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
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
name|HsController
name|controller
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|HsController
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
name|getApplicationID
argument_list|()
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
DECL|method|testJobView ()
annotation|@
name|Test
specifier|public
name|void
name|testJobView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsJobPage"
argument_list|)
expr_stmt|;
name|AppContext
name|appContext
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|TestAMWebApp
operator|.
name|getJobParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsJobPage
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
annotation|@
name|Test
DECL|method|testTasksView ()
specifier|public
name|void
name|testTasksView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsTasksPage"
argument_list|)
expr_stmt|;
name|AppContext
name|appContext
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|TestAMWebApp
operator|.
name|getTaskParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsTasksPage
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
annotation|@
name|Test
DECL|method|testTasksViewNaturalSortType ()
specifier|public
name|void
name|testTasksViewNaturalSortType
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsTasksPage"
argument_list|)
expr_stmt|;
name|AppContext
name|appContext
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|TestAMWebApp
operator|.
name|getTaskParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|Injector
name|testPage
init|=
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsTasksPage
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
decl_stmt|;
name|View
name|viewInstance
init|=
name|testPage
operator|.
name|getInstance
argument_list|(
name|HsTasksPage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moreParams
init|=
name|viewInstance
operator|.
name|context
argument_list|()
operator|.
name|requestContext
argument_list|()
operator|.
name|moreParams
argument_list|()
decl_stmt|;
name|String
name|appTableColumnsMeta
init|=
name|moreParams
operator|.
name|get
argument_list|(
literal|"ui.dataTables.selector.init"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appTableColumnsMeta
operator|.
name|indexOf
argument_list|(
literal|"natural"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTaskView ()
specifier|public
name|void
name|testTaskView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsTaskPage"
argument_list|)
expr_stmt|;
name|AppContext
name|appContext
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|TestAMWebApp
operator|.
name|getTaskParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsTaskPage
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
annotation|@
name|Test
DECL|method|testTaskViewNaturalSortType ()
specifier|public
name|void
name|testTaskViewNaturalSortType
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsTaskPage"
argument_list|)
expr_stmt|;
name|AppContext
name|appContext
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|TestAMWebApp
operator|.
name|getTaskParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|Injector
name|testPage
init|=
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsTaskPage
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
decl_stmt|;
name|View
name|viewInstance
init|=
name|testPage
operator|.
name|getInstance
argument_list|(
name|HsTaskPage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|moreParams
init|=
name|viewInstance
operator|.
name|context
argument_list|()
operator|.
name|requestContext
argument_list|()
operator|.
name|moreParams
argument_list|()
decl_stmt|;
name|String
name|appTableColumnsMeta
init|=
name|moreParams
operator|.
name|get
argument_list|(
literal|"ui.dataTables.attempts.init"
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|appTableColumnsMeta
operator|.
name|indexOf
argument_list|(
literal|"natural"
argument_list|)
operator|!=
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testAttemptsWithJobView ()
annotation|@
name|Test
specifier|public
name|void
name|testAttemptsWithJobView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsAttemptsPage with data"
argument_list|)
expr_stmt|;
name|MockAppContext
name|ctx
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|JobId
name|id
init|=
name|ctx
operator|.
name|getAllJobs
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
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
name|JOB_ID
argument_list|,
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|TASK_TYPE
argument_list|,
literal|"m"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ATTEMPT_STATE
argument_list|,
literal|"SUCCESSFUL"
argument_list|)
expr_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsAttemptsPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
name|ctx
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|testAttemptsView ()
annotation|@
name|Test
specifier|public
name|void
name|testAttemptsView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsAttemptsPage"
argument_list|)
expr_stmt|;
name|AppContext
name|appContext
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|TestAMWebApp
operator|.
name|getTaskParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsAttemptsPage
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
DECL|method|testConfView ()
annotation|@
name|Test
specifier|public
name|void
name|testConfView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsConfPage"
argument_list|)
expr_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsConfPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAboutView ()
annotation|@
name|Test
specifier|public
name|void
name|testAboutView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsAboutPage"
argument_list|)
expr_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsAboutPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJobCounterView ()
annotation|@
name|Test
specifier|public
name|void
name|testJobCounterView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"JobCounterView"
argument_list|)
expr_stmt|;
name|AppContext
name|appContext
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|TestAMWebApp
operator|.
name|getJobParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsCountersPage
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
DECL|method|testJobCounterViewForKilledJob ()
annotation|@
name|Test
specifier|public
name|void
name|testJobCounterViewForKilledJob
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"JobCounterViewForKilledJob"
argument_list|)
expr_stmt|;
name|AppContext
name|appContext
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
init|=
name|TestAMWebApp
operator|.
name|getJobParams
argument_list|(
name|appContext
argument_list|)
decl_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsCountersPage
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
DECL|method|testSingleCounterView ()
annotation|@
name|Test
specifier|public
name|void
name|testSingleCounterView
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsSingleCounterPage"
argument_list|)
expr_stmt|;
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|HsSingleCounterPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLogsView1 ()
specifier|public
name|void
name|testLogsView1
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsLogsPage"
argument_list|)
expr_stmt|;
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|AggregatedLogsPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|PrintWriter
name|spyPw
init|=
name|WebAppTests
operator|.
name|getPrintWriter
argument_list|(
name|injector
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|spyPw
argument_list|)
operator|.
name|write
argument_list|(
literal|"Cannot get container logs without a ContainerId"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|spyPw
argument_list|)
operator|.
name|write
argument_list|(
literal|"Cannot get container logs without a NodeId"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|spyPw
argument_list|)
operator|.
name|write
argument_list|(
literal|"Cannot get container logs without an app owner"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLogsView2 ()
specifier|public
name|void
name|testLogsView2
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsLogsPage with data"
argument_list|)
expr_stmt|;
name|MockAppContext
name|ctx
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
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
name|CONTAINER_ID
argument_list|,
name|MRApp
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|333
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|NM_NODENAME
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
name|MockJobs
operator|.
name|NM_HOST
argument_list|,
name|MockJobs
operator|.
name|NM_PORT
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ENTITY_STRING
argument_list|,
literal|"container_10_0001_01_000001"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|APP_OWNER
argument_list|,
literal|"owner"
argument_list|)
expr_stmt|;
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|AggregatedLogsPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
name|ctx
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|PrintWriter
name|spyPw
init|=
name|WebAppTests
operator|.
name|getPrintWriter
argument_list|(
name|injector
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|spyPw
argument_list|)
operator|.
name|write
argument_list|(
literal|"Aggregation is not enabled. Try the nodemanager at "
operator|+
name|MockJobs
operator|.
name|NM_HOST
operator|+
literal|":"
operator|+
name|MockJobs
operator|.
name|NM_PORT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLogsViewSingle ()
specifier|public
name|void
name|testLogsViewSingle
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsLogsPage with params for single log and data limits"
argument_list|)
expr_stmt|;
name|MockAppContext
name|ctx
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
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
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|LOG_AGGREGATION_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"start"
argument_list|,
literal|"-2048"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"end"
argument_list|,
literal|"-1024"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|CONTAINER_LOG_TYPE
argument_list|,
literal|"syslog"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|CONTAINER_ID
argument_list|,
name|MRApp
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|333
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|NM_NODENAME
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
name|MockJobs
operator|.
name|NM_HOST
argument_list|,
name|MockJobs
operator|.
name|NM_PORT
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ENTITY_STRING
argument_list|,
literal|"container_10_0001_01_000001"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|APP_OWNER
argument_list|,
literal|"owner"
argument_list|)
expr_stmt|;
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|AggregatedLogsPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
name|ctx
argument_list|,
name|params
argument_list|,
operator|new
name|AbstractModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|PrintWriter
name|spyPw
init|=
name|WebAppTests
operator|.
name|getPrintWriter
argument_list|(
name|injector
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|spyPw
argument_list|)
operator|.
name|write
argument_list|(
literal|"Logs not available for container_10_0001_01_000001."
operator|+
literal|" Aggregation may not be complete, Check back later or try to"
operator|+
literal|" find the container logs in the local directory of nodemanager "
operator|+
name|MockJobs
operator|.
name|NM_HOST
operator|+
literal|":"
operator|+
name|MockJobs
operator|.
name|NM_PORT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLogsViewBadStartEnd ()
specifier|public
name|void
name|testLogsViewBadStartEnd
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"HsLogsPage with bad start/end params"
argument_list|)
expr_stmt|;
name|MockAppContext
name|ctx
init|=
operator|new
name|MockAppContext
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
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
literal|"start"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
literal|"end"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|CONTAINER_ID
argument_list|,
name|MRApp
operator|.
name|newContainerId
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|333
argument_list|,
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|NM_NODENAME
argument_list|,
name|NodeId
operator|.
name|newInstance
argument_list|(
name|MockJobs
operator|.
name|NM_HOST
argument_list|,
name|MockJobs
operator|.
name|NM_PORT
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|ENTITY_STRING
argument_list|,
literal|"container_10_0001_01_000001"
argument_list|)
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|APP_OWNER
argument_list|,
literal|"owner"
argument_list|)
expr_stmt|;
name|Injector
name|injector
init|=
name|WebAppTests
operator|.
name|testPage
argument_list|(
name|AggregatedLogsPage
operator|.
name|class
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
name|ctx
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|PrintWriter
name|spyPw
init|=
name|WebAppTests
operator|.
name|getPrintWriter
argument_list|(
name|injector
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|spyPw
argument_list|)
operator|.
name|write
argument_list|(
literal|"Invalid log start value: foo"
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|spyPw
argument_list|)
operator|.
name|write
argument_list|(
literal|"Invalid log end value: bar"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

