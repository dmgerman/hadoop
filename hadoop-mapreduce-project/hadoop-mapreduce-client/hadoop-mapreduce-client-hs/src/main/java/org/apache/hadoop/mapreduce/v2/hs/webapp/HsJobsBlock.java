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
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
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
name|lang3
operator|.
name|StringEscapeUtils
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
name|MRConfig
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
name|hs
operator|.
name|webapp
operator|.
name|dao
operator|.
name|JobInfo
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|util
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
name|hamlet2
operator|.
name|Hamlet
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
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TABLE
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
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TBODY
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
name|hamlet2
operator|.
name|HamletSpec
operator|.
name|InputType
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
name|view
operator|.
name|HtmlBlock
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
comment|/**  * Render all of the jobs that the history server is aware of.  */
end_comment

begin_class
DECL|class|HsJobsBlock
specifier|public
class|class
name|HsJobsBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|appContext
specifier|final
name|AppContext
name|appContext
decl_stmt|;
DECL|field|dateFormat
specifier|final
name|SimpleDateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy.MM.dd HH:mm:ss z"
argument_list|)
decl_stmt|;
DECL|field|ugi
specifier|private
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|isFilterAppListByUserEnabled
specifier|private
name|boolean
name|isFilterAppListByUserEnabled
decl_stmt|;
DECL|field|areAclsEnabled
specifier|private
name|boolean
name|areAclsEnabled
decl_stmt|;
DECL|field|adminAclList
specifier|private
name|AccessControlList
name|adminAclList
decl_stmt|;
annotation|@
name|Inject
DECL|method|HsJobsBlock (Configuration conf, AppContext appCtx, ViewContext ctx)
name|HsJobsBlock
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|appCtx
parameter_list|,
name|ViewContext
name|ctx
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|appContext
operator|=
name|appCtx
expr_stmt|;
name|isFilterAppListByUserEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|FILTER_ENTITY_LIST_BY_USER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|areAclsEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRConfig
operator|.
name|MR_ACLS_ENABLED
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|adminAclList
operator|=
operator|new
name|AccessControlList
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|MRConfig
operator|.
name|MR_ADMINS
argument_list|,
literal|" "
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.yarn.webapp.view.HtmlBlock#render(org.apache.hadoop.yarn.webapp.view.HtmlBlock.Block)    */
DECL|method|render (Block html)
annotation|@
name|Override
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|tbody
init|=
name|html
operator|.
name|h2
argument_list|(
literal|"Retired Jobs"
argument_list|)
operator|.
name|table
argument_list|(
literal|"#jobs"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|"Submit Time"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Start Time"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Finish Time"
argument_list|)
operator|.
name|th
argument_list|(
literal|".id"
argument_list|,
literal|"Job ID"
argument_list|)
operator|.
name|th
argument_list|(
literal|".name"
argument_list|,
literal|"Name"
argument_list|)
operator|.
name|th
argument_list|(
literal|"User"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Queue"
argument_list|)
operator|.
name|th
argument_list|(
literal|".state"
argument_list|,
literal|"State"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Maps Total"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Maps Completed"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Reduces Total"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Reduces Completed"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Elapsed Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Getting list of all Jobs."
argument_list|)
expr_stmt|;
comment|// Write all the data into a JavaScript array of arrays for JQuery
comment|// DataTables to display
name|StringBuilder
name|jobsTableData
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"[\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|Job
name|j
range|:
name|appContext
operator|.
name|getAllJobs
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|JobInfo
name|job
init|=
operator|new
name|JobInfo
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|ugi
operator|=
name|getCallerUGI
argument_list|()
expr_stmt|;
comment|// Allow to list only per-user apps if incoming ugi has permission.
if|if
condition|(
name|isFilterAppListByUserEnabled
operator|&&
name|ugi
operator|!=
literal|null
operator|&&
operator|!
name|checkAccess
argument_list|(
name|job
operator|.
name|getUserName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|jobsTableData
operator|.
name|append
argument_list|(
literal|"[\""
argument_list|)
operator|.
name|append
argument_list|(
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|job
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|job
operator|.
name|getFormattedStartTimeStr
argument_list|(
name|dateFormat
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|dateFormat
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|job
operator|.
name|getFinishTime
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
literal|"<a href='"
argument_list|)
operator|.
name|append
argument_list|(
name|url
argument_list|(
literal|"job"
argument_list|,
name|job
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"'>"
argument_list|)
operator|.
name|append
argument_list|(
name|job
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"</a>\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml4
argument_list|(
name|job
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml4
argument_list|(
name|job
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml4
argument_list|(
name|job
operator|.
name|getQueueName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|job
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getMapsTotal
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getMapsCompleted
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getReducesTotal
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getReducesCompleted
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringUtils
operator|.
name|formatTimeSortable
argument_list|(
name|Times
operator|.
name|elapsed
argument_list|(
name|job
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|job
operator|.
name|getFinishTime
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"],\n"
argument_list|)
expr_stmt|;
block|}
comment|//Remove the last comma and close off the array of arrays
if|if
condition|(
name|jobsTableData
operator|.
name|charAt
argument_list|(
name|jobsTableData
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
operator|==
literal|','
condition|)
block|{
name|jobsTableData
operator|.
name|delete
argument_list|(
name|jobsTableData
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|jobsTableData
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|jobsTableData
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|html
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"var jobsTableData="
operator|+
name|jobsTableData
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|tbody
operator|.
name|__
argument_list|()
operator|.
name|tfoot
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"submit_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Submit Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"start_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Start Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"finish_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Finish Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"job_id"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Job ID"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"name"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Name"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"user"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"User"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"queue"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Queue"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"state"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"State"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"maps_total"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Maps Total"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"maps_completed"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Maps Completed"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"reduces_total"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Reduces Total"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"reduces_completed"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Reduces Completed"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"elapsed_time"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"Elapsed Time"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
DECL|method|checkAccess (String userName)
specifier|private
name|boolean
name|checkAccess
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|areAclsEnabled
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// User could see its own job.
if|if
condition|(
name|ugi
operator|.
name|getShortUserName
argument_list|()
operator|.
name|equals
argument_list|(
name|userName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// Admin could also see all jobs
if|if
condition|(
name|adminAclList
operator|!=
literal|null
operator|&&
name|adminAclList
operator|.
name|isUserAllowed
argument_list|(
name|ugi
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

