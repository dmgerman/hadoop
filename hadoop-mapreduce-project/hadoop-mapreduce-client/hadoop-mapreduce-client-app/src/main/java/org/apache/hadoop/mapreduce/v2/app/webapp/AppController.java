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
name|yarn
operator|.
name|util
operator|.
name|StringHelper
operator|.
name|join
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
name|net
operator|.
name|URLDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|webapp
operator|.
name|dao
operator|.
name|AppInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|util
operator|.
name|MRWebAppUtil
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
name|util
operator|.
name|StringHelper
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
name|Times
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
name|util
operator|.
name|WebAppUtils
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
name|base
operator|.
name|Joiner
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
name|Inject
import|;
end_import

begin_comment
comment|/**  * This class renders the various pages that the web app supports.  */
end_comment

begin_class
DECL|class|AppController
specifier|public
class|class
name|AppController
extends|extends
name|Controller
implements|implements
name|AMParams
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|AppController
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|JOINER
specifier|private
specifier|static
specifier|final
name|Joiner
name|JOINER
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|""
argument_list|)
decl_stmt|;
DECL|field|app
specifier|protected
specifier|final
name|App
name|app
decl_stmt|;
DECL|method|AppController (App app, Configuration conf, RequestContext ctx, String title)
specifier|protected
name|AppController
parameter_list|(
name|App
name|app
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|RequestContext
name|ctx
parameter_list|,
name|String
name|title
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|app
operator|=
name|app
expr_stmt|;
name|set
argument_list|(
name|APP_ID
argument_list|,
name|app
operator|.
name|context
operator|.
name|getApplicationID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|RM_WEB
argument_list|,
name|JOINER
operator|.
name|join
argument_list|(
name|MRWebAppUtil
operator|.
name|getYARNWebappScheme
argument_list|()
argument_list|,
name|WebAppUtils
operator|.
name|getResolvedRMWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|,
name|MRWebAppUtil
operator|.
name|getYARNHttpPolicy
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Inject
DECL|method|AppController (App app, Configuration conf, RequestContext ctx)
specifier|protected
name|AppController
parameter_list|(
name|App
name|app
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|RequestContext
name|ctx
parameter_list|)
block|{
name|this
argument_list|(
name|app
argument_list|,
name|conf
argument_list|,
name|ctx
argument_list|,
literal|"am"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Render the default(index.html) page for the Application Controller    */
DECL|method|index ()
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|()
block|{
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"MapReduce Application "
argument_list|,
name|$
argument_list|(
name|APP_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Render the /info page with an overview of current application.    */
DECL|method|info ()
specifier|public
name|void
name|info
parameter_list|()
block|{
name|AppInfo
name|info
init|=
operator|new
name|AppInfo
argument_list|(
name|app
argument_list|,
name|app
operator|.
name|context
argument_list|)
decl_stmt|;
name|info
argument_list|(
literal|"Application Master Overview"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Application ID:"
argument_list|,
name|info
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Application Name:"
argument_list|,
name|info
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"User:"
argument_list|,
name|info
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Started on:"
argument_list|,
name|Times
operator|.
name|format
argument_list|(
name|info
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Elasped: "
argument_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|info
operator|.
name|getElapsedTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|InfoPage
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return The class that will render the /job page    */
DECL|method|jobPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|jobPage
parameter_list|()
block|{
return|return
name|JobPage
operator|.
name|class
return|;
block|}
comment|/**    * Render the /job page    */
DECL|method|job ()
specifier|public
name|void
name|job
parameter_list|()
block|{
try|try
block|{
name|requireJob
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|render
argument_list|(
name|jobPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the class that will render the /jobcounters page    */
DECL|method|countersPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|countersPage
parameter_list|()
block|{
return|return
name|CountersPage
operator|.
name|class
return|;
block|}
comment|/**    * Render the /jobcounters page    */
DECL|method|jobCounters ()
specifier|public
name|void
name|jobCounters
parameter_list|()
block|{
try|try
block|{
name|requireJob
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|app
operator|.
name|getJob
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"Counters for "
argument_list|,
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|render
argument_list|(
name|countersPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Display a page showing a task's counters    */
DECL|method|taskCounters ()
specifier|public
name|void
name|taskCounters
parameter_list|()
block|{
try|try
block|{
name|requireTask
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|app
operator|.
name|getTask
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setTitle
argument_list|(
name|StringHelper
operator|.
name|join
argument_list|(
literal|"Counters for "
argument_list|,
name|$
argument_list|(
name|TASK_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|render
argument_list|(
name|countersPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the class that will render the /singlejobcounter page    */
DECL|method|singleCounterPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|singleCounterPage
parameter_list|()
block|{
return|return
name|SingleCounterPage
operator|.
name|class
return|;
block|}
comment|/**    * Render the /singlejobcounter page    * @throws IOException on any error.    */
DECL|method|singleJobCounter ()
specifier|public
name|void
name|singleJobCounter
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|requireJob
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|set
argument_list|(
name|COUNTER_GROUP
argument_list|,
name|URLDecoder
operator|.
name|decode
argument_list|(
name|$
argument_list|(
name|COUNTER_GROUP
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|COUNTER_NAME
argument_list|,
name|URLDecoder
operator|.
name|decode
argument_list|(
name|$
argument_list|(
name|COUNTER_NAME
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|app
operator|.
name|getJob
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setTitle
argument_list|(
name|StringHelper
operator|.
name|join
argument_list|(
name|$
argument_list|(
name|COUNTER_GROUP
argument_list|)
argument_list|,
literal|" "
argument_list|,
name|$
argument_list|(
name|COUNTER_NAME
argument_list|)
argument_list|,
literal|" for "
argument_list|,
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|render
argument_list|(
name|singleCounterPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Render the /singletaskcounter page    * @throws IOException on any error.    */
DECL|method|singleTaskCounter ()
specifier|public
name|void
name|singleTaskCounter
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|requireTask
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|set
argument_list|(
name|COUNTER_GROUP
argument_list|,
name|URLDecoder
operator|.
name|decode
argument_list|(
name|$
argument_list|(
name|COUNTER_GROUP
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|COUNTER_NAME
argument_list|,
name|URLDecoder
operator|.
name|decode
argument_list|(
name|$
argument_list|(
name|COUNTER_NAME
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|app
operator|.
name|getTask
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setTitle
argument_list|(
name|StringHelper
operator|.
name|join
argument_list|(
name|$
argument_list|(
name|COUNTER_GROUP
argument_list|)
argument_list|,
literal|" "
argument_list|,
name|$
argument_list|(
name|COUNTER_NAME
argument_list|)
argument_list|,
literal|" for "
argument_list|,
name|$
argument_list|(
name|TASK_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|render
argument_list|(
name|singleCounterPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the class that will render the /tasks page    */
DECL|method|tasksPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|tasksPage
parameter_list|()
block|{
return|return
name|TasksPage
operator|.
name|class
return|;
block|}
comment|/**    * Render the /tasks page    */
DECL|method|tasks ()
specifier|public
name|void
name|tasks
parameter_list|()
block|{
try|try
block|{
name|requireJob
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|app
operator|.
name|getJob
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|String
name|tt
init|=
name|$
argument_list|(
name|TASK_TYPE
argument_list|)
decl_stmt|;
name|tt
operator|=
name|tt
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"All"
else|:
name|StringUtils
operator|.
name|capitalize
argument_list|(
name|MRApps
operator|.
name|taskType
argument_list|(
name|tt
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
expr_stmt|;
name|setTitle
argument_list|(
name|join
argument_list|(
name|tt
argument_list|,
literal|" Tasks for "
argument_list|,
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to render tasks page with task type : "
operator|+
name|$
argument_list|(
name|TASK_TYPE
argument_list|)
operator|+
literal|" for job id : "
operator|+
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|badRequest
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|render
argument_list|(
name|tasksPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the class that will render the /task page    */
DECL|method|taskPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|taskPage
parameter_list|()
block|{
return|return
name|TaskPage
operator|.
name|class
return|;
block|}
comment|/**    * Render the /task page    */
DECL|method|task ()
specifier|public
name|void
name|task
parameter_list|()
block|{
try|try
block|{
name|requireTask
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|app
operator|.
name|getTask
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"Attempts for "
argument_list|,
name|$
argument_list|(
name|TASK_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|render
argument_list|(
name|taskPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the class that will render the /attempts page    */
DECL|method|attemptsPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|attemptsPage
parameter_list|()
block|{
return|return
name|AttemptsPage
operator|.
name|class
return|;
block|}
comment|/**    * Render the attempts page    */
DECL|method|attempts ()
specifier|public
name|void
name|attempts
parameter_list|()
block|{
try|try
block|{
name|requireJob
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|app
operator|.
name|getJob
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|String
name|taskType
init|=
name|$
argument_list|(
name|TASK_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskType
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"missing task-type."
argument_list|)
throw|;
block|}
name|String
name|attemptState
init|=
name|$
argument_list|(
name|ATTEMPT_STATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|attemptState
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"missing attempt-state."
argument_list|)
throw|;
block|}
name|setTitle
argument_list|(
name|join
argument_list|(
name|attemptState
argument_list|,
literal|" "
argument_list|,
name|MRApps
operator|.
name|taskType
argument_list|(
name|taskType
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
literal|" attempts in "
argument_list|,
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|attemptsPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to render attempts page with task type : "
operator|+
name|$
argument_list|(
name|TASK_TYPE
argument_list|)
operator|+
literal|" for job id : "
operator|+
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|badRequest
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @return the page that will be used to render the /conf page    */
DECL|method|confPage ()
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|View
argument_list|>
name|confPage
parameter_list|()
block|{
return|return
name|JobConfPage
operator|.
name|class
return|;
block|}
comment|/**    * Render the /conf page    */
DECL|method|conf ()
specifier|public
name|void
name|conf
parameter_list|()
block|{
try|try
block|{
name|requireJob
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|renderText
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|render
argument_list|(
name|confPage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Render a BAD_REQUEST error.    * @param s the error message to include.    */
DECL|method|badRequest (String s)
name|void
name|badRequest
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
expr_stmt|;
name|String
name|title
init|=
literal|"Bad request: "
decl_stmt|;
name|setTitle
argument_list|(
operator|(
name|s
operator|!=
literal|null
operator|)
condition|?
name|join
argument_list|(
name|title
argument_list|,
name|s
argument_list|)
else|:
name|title
argument_list|)
expr_stmt|;
block|}
comment|/**    * Render a NOT_FOUND error.    * @param s the error message to include.    */
DECL|method|notFound (String s)
name|void
name|notFound
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"Not found: "
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Render a ACCESS_DENIED error.    * @param s the error message to include.    */
DECL|method|accessDenied (String s)
name|void
name|accessDenied
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"Access denied: "
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * check for job access.    * @param job the job that is being accessed    * @return True if the requesting user has permission to view the job    */
DECL|method|checkAccess (Job job)
name|boolean
name|checkAccess
parameter_list|(
name|Job
name|job
parameter_list|)
block|{
name|String
name|remoteUser
init|=
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|callerUGI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remoteUser
operator|!=
literal|null
condition|)
block|{
name|callerUGI
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|remoteUser
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|callerUGI
operator|!=
literal|null
operator|&&
operator|!
name|job
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * Ensure that a JOB_ID was passed into the page.    */
DECL|method|requireJob ()
specifier|public
name|void
name|requireJob
parameter_list|()
block|{
if|if
condition|(
name|$
argument_list|(
name|JOB_ID
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|badRequest
argument_list|(
literal|"missing job ID"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Bad Request: Missing job ID"
argument_list|)
throw|;
block|}
name|JobId
name|jobID
init|=
name|MRApps
operator|.
name|toJobID
argument_list|(
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
decl_stmt|;
name|app
operator|.
name|setJob
argument_list|(
name|app
operator|.
name|context
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|app
operator|.
name|getJob
argument_list|()
operator|==
literal|null
condition|)
block|{
name|notFound
argument_list|(
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not Found: "
operator|+
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
throw|;
block|}
comment|/* check for acl access */
name|Job
name|job
init|=
name|app
operator|.
name|context
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|checkAccess
argument_list|(
name|job
argument_list|)
condition|)
block|{
name|accessDenied
argument_list|(
literal|"User "
operator|+
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
operator|+
literal|" does not have "
operator|+
literal|" permission to view job "
operator|+
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Access denied: User "
operator|+
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
operator|+
literal|" does not have permission to view job "
operator|+
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**    * Ensure that a TASK_ID was passed into the page.    */
DECL|method|requireTask ()
specifier|public
name|void
name|requireTask
parameter_list|()
block|{
if|if
condition|(
name|$
argument_list|(
name|TASK_ID
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|badRequest
argument_list|(
literal|"missing task ID"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"missing task ID"
argument_list|)
throw|;
block|}
name|TaskId
name|taskID
init|=
name|MRApps
operator|.
name|toTaskID
argument_list|(
name|$
argument_list|(
name|TASK_ID
argument_list|)
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|app
operator|.
name|context
operator|.
name|getJob
argument_list|(
name|taskID
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|app
operator|.
name|setJob
argument_list|(
name|job
argument_list|)
expr_stmt|;
if|if
condition|(
name|app
operator|.
name|getJob
argument_list|()
operator|==
literal|null
condition|)
block|{
name|notFound
argument_list|(
name|MRApps
operator|.
name|toString
argument_list|(
name|taskID
operator|.
name|getJobId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not Found: "
operator|+
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
name|app
operator|.
name|setTask
argument_list|(
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getTask
argument_list|(
name|taskID
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|app
operator|.
name|getTask
argument_list|()
operator|==
literal|null
condition|)
block|{
name|notFound
argument_list|(
name|$
argument_list|(
name|TASK_ID
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not Found: "
operator|+
name|$
argument_list|(
name|TASK_ID
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|checkAccess
argument_list|(
name|job
argument_list|)
condition|)
block|{
name|accessDenied
argument_list|(
literal|"User "
operator|+
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
operator|+
literal|" does not have "
operator|+
literal|" permission to view job "
operator|+
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Access denied: User "
operator|+
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
operator|+
literal|" does not have permission to view job "
operator|+
name|$
argument_list|(
name|JOB_ID
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

