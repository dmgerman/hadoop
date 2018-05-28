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
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|_EVEN
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
name|view
operator|.
name|JQueryUI
operator|.
name|_INFO_WRAP
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
name|view
operator|.
name|JQueryUI
operator|.
name|_ODD
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
name|view
operator|.
name|JQueryUI
operator|.
name|_TH
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
name|java
operator|.
name|util
operator|.
name|List
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
name|TaskID
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
name|AMInfo
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
name|ConfEntryInfo
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
name|UnparsedJob
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
name|AMAttemptInfo
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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JHAdminConfig
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
name|MRApps
operator|.
name|TaskAttemptStateUI
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
name|ResponseInfo
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
name|DIV
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
name|view
operator|.
name|HtmlBlock
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
name|InfoBlock
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
comment|/**  * Render a block of HTML for a give job.  */
end_comment

begin_class
DECL|class|HsJobBlock
specifier|public
class|class
name|HsJobBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|appContext
specifier|final
name|AppContext
name|appContext
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
DECL|method|HsJobBlock (Configuration conf, AppContext appctx, ViewContext ctx)
annotation|@
name|Inject
name|HsJobBlock
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|AppContext
name|appctx
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
name|appctx
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
name|String
name|jid
init|=
name|$
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|jid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"Sorry, can't do anything without a JobID."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return;
block|}
name|JobId
name|jobID
init|=
name|MRApps
operator|.
name|toJobID
argument_list|(
name|jid
argument_list|)
decl_stmt|;
name|Job
name|j
init|=
name|appContext
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
if|if
condition|(
name|j
operator|==
literal|null
condition|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"Sorry, "
argument_list|,
name|jid
argument_list|,
literal|" not found."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return;
block|}
name|ugi
operator|=
name|getCallerUGI
argument_list|()
expr_stmt|;
if|if
condition|(
name|isFilterAppListByUserEnabled
operator|&&
name|ugi
operator|!=
literal|null
operator|&&
operator|!
name|j
operator|.
name|checkAccess
argument_list|(
name|ugi
argument_list|,
name|JobACL
operator|.
name|VIEW_JOB
argument_list|)
condition|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"Sorry, "
argument_list|,
name|jid
argument_list|,
literal|" could not be viewed for '"
argument_list|,
name|ugi
operator|.
name|getUserName
argument_list|()
argument_list|,
literal|"'."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|j
operator|instanceof
name|UnparsedJob
condition|)
block|{
specifier|final
name|int
name|taskCount
init|=
name|j
operator|.
name|getTotalMaps
argument_list|()
operator|+
name|j
operator|.
name|getTotalReduces
argument_list|()
decl_stmt|;
name|UnparsedJob
name|oversizedJob
init|=
operator|(
name|UnparsedJob
operator|)
name|j
decl_stmt|;
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"The job has a total of "
operator|+
name|taskCount
operator|+
literal|" tasks. "
argument_list|)
operator|.
name|__
argument_list|(
literal|"Any job larger than "
operator|+
name|oversizedJob
operator|.
name|getMaxTasksAllowed
argument_list|()
operator|+
literal|" will not be loaded."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|html
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"You can either use the CLI tool: 'mapred job -history'"
operator|+
literal|" to view large jobs or adjust the property "
operator|+
name|JHAdminConfig
operator|.
name|MR_HS_LOADED_JOBS_TASKS_MAX
operator|+
literal|"."
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
return|return;
block|}
name|List
argument_list|<
name|AMInfo
argument_list|>
name|amInfos
init|=
name|j
operator|.
name|getAMInfos
argument_list|()
decl_stmt|;
name|JobInfo
name|job
init|=
operator|new
name|JobInfo
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|ResponseInfo
name|infoBlock
init|=
name|info
argument_list|(
literal|"Job Overview"
argument_list|)
operator|.
name|__
argument_list|(
literal|"Job Name:"
argument_list|,
name|job
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"User Name:"
argument_list|,
name|job
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Queue:"
argument_list|,
name|job
operator|.
name|getQueueName
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"State:"
argument_list|,
name|job
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Uberized:"
argument_list|,
name|job
operator|.
name|isUber
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Submitted:"
argument_list|,
operator|new
name|Date
argument_list|(
name|job
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Started:"
argument_list|,
name|job
operator|.
name|getStartTimeStr
argument_list|()
argument_list|)
operator|.
name|__
argument_list|(
literal|"Finished:"
argument_list|,
operator|new
name|Date
argument_list|(
name|job
operator|.
name|getFinishTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|(
literal|"Elapsed:"
argument_list|,
name|StringUtils
operator|.
name|formatTime
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
decl_stmt|;
name|String
name|amString
init|=
name|amInfos
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
literal|"ApplicationMaster"
else|:
literal|"ApplicationMasters"
decl_stmt|;
comment|// todo - switch to use JobInfo
name|List
argument_list|<
name|String
argument_list|>
name|diagnostics
init|=
name|j
operator|.
name|getDiagnostics
argument_list|()
decl_stmt|;
if|if
condition|(
name|diagnostics
operator|!=
literal|null
operator|&&
operator|!
name|diagnostics
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuffer
name|b
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|diag
range|:
name|diagnostics
control|)
block|{
name|b
operator|.
name|append
argument_list|(
name|addTaskLinks
argument_list|(
name|diag
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|infoBlock
operator|.
name|_r
argument_list|(
literal|"Diagnostics:"
argument_list|,
name|b
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|job
operator|.
name|getNumMaps
argument_list|()
operator|>
literal|0
condition|)
block|{
name|infoBlock
operator|.
name|__
argument_list|(
literal|"Average Map Time"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|job
operator|.
name|getAvgMapTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|job
operator|.
name|getNumReduces
argument_list|()
operator|>
literal|0
condition|)
block|{
name|infoBlock
operator|.
name|__
argument_list|(
literal|"Average Shuffle Time"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|job
operator|.
name|getAvgShuffleTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|infoBlock
operator|.
name|__
argument_list|(
literal|"Average Merge Time"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|job
operator|.
name|getAvgMergeTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|infoBlock
operator|.
name|__
argument_list|(
literal|"Average Reduce Time"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|job
operator|.
name|getAvgReduceTime
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ConfEntryInfo
name|entry
range|:
name|job
operator|.
name|getAcls
argument_list|()
control|)
block|{
name|infoBlock
operator|.
name|__
argument_list|(
literal|"ACL "
operator|+
name|entry
operator|.
name|getName
argument_list|()
operator|+
literal|":"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|div
init|=
name|html
operator|.
name|__
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
decl_stmt|;
comment|// MRAppMasters Table
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|table
init|=
name|div
operator|.
name|table
argument_list|(
literal|"#job"
argument_list|)
decl_stmt|;
name|table
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|amString
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Attempt Number"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Start Time"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Node"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Logs"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
name|boolean
name|odd
init|=
literal|false
decl_stmt|;
for|for
control|(
name|AMInfo
name|amInfo
range|:
name|amInfos
control|)
block|{
name|AMAttemptInfo
name|attempt
init|=
operator|new
name|AMAttemptInfo
argument_list|(
name|amInfo
argument_list|,
name|job
operator|.
name|getId
argument_list|()
argument_list|,
name|job
operator|.
name|getUserName
argument_list|()
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|table
operator|.
name|tr
argument_list|(
operator|(
name|odd
operator|=
operator|!
name|odd
operator|)
condition|?
name|_ODD
else|:
name|_EVEN
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|attempt
operator|.
name|getAttemptId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
operator|new
name|Date
argument_list|(
name|attempt
operator|.
name|getStartTime
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
literal|".nodelink"
argument_list|,
name|url
argument_list|(
name|MRWebAppUtil
operator|.
name|getYARNWebappScheme
argument_list|()
argument_list|,
name|attempt
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
argument_list|,
name|attempt
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
literal|".logslink"
argument_list|,
name|url
argument_list|(
name|attempt
operator|.
name|getLogsLink
argument_list|()
argument_list|)
argument_list|,
literal|"logs"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|table
operator|.
name|__
argument_list|()
expr_stmt|;
name|div
operator|.
name|__
argument_list|()
expr_stmt|;
name|html
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
operator|.
comment|// Tasks table
name|table
argument_list|(
literal|"#job"
argument_list|)
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Task Type"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Total"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Complete"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|tr
argument_list|(
name|_ODD
argument_list|)
operator|.
name|th
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"tasks"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|)
argument_list|,
literal|"Map"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
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
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
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
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|tr
argument_list|(
name|_EVEN
argument_list|)
operator|.
name|th
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"tasks"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|)
argument_list|,
literal|"Reduce"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
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
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
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
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
comment|// Attempts table
name|table
argument_list|(
literal|"#job"
argument_list|)
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Attempt Type"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Failed"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Killed"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Successful"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|tr
argument_list|(
name|_ODD
argument_list|)
operator|.
name|th
argument_list|(
literal|"Maps"
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|FAILED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getFailedMapAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|KILLED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getKilledMapAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"m"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|SUCCESSFUL
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getSuccessfulMapAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|tr
argument_list|(
name|_EVEN
argument_list|)
operator|.
name|th
argument_list|(
literal|"Reduces"
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|FAILED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getFailedReduceAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|KILLED
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getKilledReduceAttempts
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
name|url
argument_list|(
literal|"attempts"
argument_list|,
name|jid
argument_list|,
literal|"r"
argument_list|,
name|TaskAttemptStateUI
operator|.
name|SUCCESSFUL
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|job
operator|.
name|getSuccessfulReduceAttempts
argument_list|()
argument_list|)
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
expr_stmt|;
block|}
DECL|method|addTaskLinks (String text)
specifier|static
name|String
name|addTaskLinks
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
name|TaskID
operator|.
name|taskIdPattern
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"<a href=\"/jobhistory/task/$0\">$0</a>"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

