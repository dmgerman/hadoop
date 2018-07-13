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
name|webapp
operator|.
name|view
operator|.
name|JQueryUI
operator|.
name|ACCORDION
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
name|DATATABLES
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
name|DATATABLES_ID
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
name|initID
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
name|tableInit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|text
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
name|TaskAttemptState
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
name|TaskAttempt
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
name|TaskAttemptInfo
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
name|webapp
operator|.
name|SubView
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
name|Hamlet
operator|.
name|THEAD
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
name|TR
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

begin_class
DECL|class|TaskPage
specifier|public
class|class
name|TaskPage
extends|extends
name|AppView
block|{
DECL|class|AttemptsBlock
specifier|static
class|class
name|AttemptsBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|app
specifier|final
name|App
name|app
decl_stmt|;
DECL|field|enableUIActions
specifier|final
name|boolean
name|enableUIActions
decl_stmt|;
annotation|@
name|Inject
DECL|method|AttemptsBlock (App ctx, Configuration conf)
name|AttemptsBlock
parameter_list|(
name|App
name|ctx
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|app
operator|=
name|ctx
expr_stmt|;
name|this
operator|.
name|enableUIActions
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRConfig
operator|.
name|MASTER_WEBAPP_UI_ACTIONS_ENABLED
argument_list|,
name|MRConfig
operator|.
name|DEFAULT_MASTER_WEBAPP_UI_ACTIONS_ENABLED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isValidRequest
argument_list|()
condition|)
block|{
name|html
operator|.
name|h2
argument_list|(
name|$
argument_list|(
name|TITLE
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|JobId
name|jobId
init|=
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getID
argument_list|()
decl_stmt|;
if|if
condition|(
name|enableUIActions
condition|)
block|{
comment|// Kill task attempt
name|StringBuilder
name|script
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|script
operator|.
name|append
argument_list|(
literal|"function confirmAction(appID, jobID, taskID, attID) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"  var b = confirm(\"Are you sure?\");\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"  if (b == true) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    var current = '/proxy/' + appID"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      + '/mapreduce/task/' + taskID;\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    var stateURL = '/proxy/' + appID"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      + '/ws/v1/mapreduce/jobs/' + jobID"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      + '/tasks/' + taskID"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      + '/attempts/' + attID + '/state';\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    $.ajax({\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      type: 'PUT',\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      url: stateURL,\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      contentType: 'application/json',\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      data: '{\"state\":\"KILLED\"}',\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      dataType: 'json'\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    }).done(function(data) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"         setTimeout(function() {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"           location.href = current;\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"         }, 1000);\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    }).fail(function(data) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"         console.log(data);\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    });\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"  }\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"}\n"
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
name|script
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
name|TR
argument_list|<
name|THEAD
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
argument_list|>
name|tr
init|=
name|html
operator|.
name|table
argument_list|(
literal|"#attempts"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
decl_stmt|;
name|tr
operator|.
name|th
argument_list|(
literal|".id"
argument_list|,
literal|"Attempt"
argument_list|)
operator|.
name|th
argument_list|(
literal|".progress"
argument_list|,
literal|"Progress"
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
literal|".status"
argument_list|,
literal|"Status"
argument_list|)
operator|.
name|th
argument_list|(
literal|".node"
argument_list|,
literal|"Node"
argument_list|)
operator|.
name|th
argument_list|(
literal|".logs"
argument_list|,
literal|"Logs"
argument_list|)
operator|.
name|th
argument_list|(
literal|".tsh"
argument_list|,
literal|"Started"
argument_list|)
operator|.
name|th
argument_list|(
literal|".tsh"
argument_list|,
literal|"Finished"
argument_list|)
operator|.
name|th
argument_list|(
literal|".tsh"
argument_list|,
literal|"Elapsed"
argument_list|)
operator|.
name|th
argument_list|(
literal|".note"
argument_list|,
literal|"Note"
argument_list|)
expr_stmt|;
if|if
condition|(
name|enableUIActions
condition|)
block|{
name|tr
operator|.
name|th
argument_list|(
literal|".actions"
argument_list|,
literal|"Actions"
argument_list|)
expr_stmt|;
block|}
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|tbody
init|=
name|tr
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
comment|// Write all the data into a JavaScript array of arrays for JQuery
comment|// DataTables to display
name|StringBuilder
name|attemptsTableData
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"[\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|TaskAttempt
name|attempt
range|:
name|getTaskAttempts
argument_list|()
control|)
block|{
name|TaskAttemptInfo
name|ta
init|=
operator|new
name|TaskAttemptInfo
argument_list|(
name|attempt
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|progress
init|=
name|StringUtils
operator|.
name|format
argument_list|(
literal|"%.2f"
argument_list|,
name|ta
operator|.
name|getProgress
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|nodeHttpAddr
init|=
name|ta
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|String
name|diag
init|=
name|ta
operator|.
name|getNote
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|ta
operator|.
name|getNote
argument_list|()
decl_stmt|;
name|TaskId
name|taskId
init|=
name|attempt
operator|.
name|getID
argument_list|()
operator|.
name|getTaskId
argument_list|()
decl_stmt|;
name|attemptsTableData
operator|.
name|append
argument_list|(
literal|"[\""
argument_list|)
operator|.
name|append
argument_list|(
name|getAttemptId
argument_list|(
name|taskId
argument_list|,
name|ta
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
name|progress
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|ta
operator|.
name|getState
argument_list|()
operator|.
name|toString
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
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml4
argument_list|(
name|ta
operator|.
name|getStatus
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
name|nodeHttpAddr
operator|==
literal|null
condition|?
literal|"N/A"
else|:
literal|"<a class='nodelink' href='"
operator|+
name|MRWebAppUtil
operator|.
name|getYARNWebappScheme
argument_list|()
operator|+
name|nodeHttpAddr
operator|+
literal|"'>"
operator|+
name|nodeHttpAddr
operator|+
literal|"</a>"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|ta
operator|.
name|getAssignedContainerId
argument_list|()
operator|==
literal|null
condition|?
literal|"N/A"
else|:
literal|"<a class='logslink' href='"
operator|+
name|url
argument_list|(
name|MRWebAppUtil
operator|.
name|getYARNWebappScheme
argument_list|()
argument_list|,
name|nodeHttpAddr
argument_list|,
literal|"node"
argument_list|,
literal|"containerlogs"
argument_list|,
name|ta
operator|.
name|getAssignedContainerIdStr
argument_list|()
argument_list|,
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|+
literal|"'>logs</a>"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|ta
operator|.
name|getStartTime
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
name|ta
operator|.
name|getFinishTime
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
name|ta
operator|.
name|getElapsedTime
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
name|StringEscapeUtils
operator|.
name|escapeEcmaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml4
argument_list|(
name|diag
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|enableUIActions
condition|)
block|{
name|attemptsTableData
operator|.
name|append
argument_list|(
literal|"\",\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|EnumSet
operator|.
name|of
argument_list|(
name|TaskAttemptState
operator|.
name|SUCCEEDED
argument_list|,
name|TaskAttemptState
operator|.
name|FAILED
argument_list|,
name|TaskAttemptState
operator|.
name|KILLED
argument_list|)
operator|.
name|contains
argument_list|(
name|attempt
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
name|attemptsTableData
operator|.
name|append
argument_list|(
literal|"N/A"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attemptsTableData
operator|.
name|append
argument_list|(
literal|"<a href=javascript:void(0) onclick=confirmAction('"
argument_list|)
operator|.
name|append
argument_list|(
name|jobId
operator|.
name|getAppId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"','"
argument_list|)
operator|.
name|append
argument_list|(
name|jobId
argument_list|)
operator|.
name|append
argument_list|(
literal|"','"
argument_list|)
operator|.
name|append
argument_list|(
name|attempt
operator|.
name|getID
argument_list|()
operator|.
name|getTaskId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"','"
argument_list|)
operator|.
name|append
argument_list|(
name|ta
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"');>Kill</a>"
argument_list|)
expr_stmt|;
block|}
name|attemptsTableData
operator|.
name|append
argument_list|(
literal|"\"],\n"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Remove the last comma and close off the array of arrays
if|if
condition|(
name|attemptsTableData
operator|.
name|charAt
argument_list|(
name|attemptsTableData
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
name|attemptsTableData
operator|.
name|delete
argument_list|(
name|attemptsTableData
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|attemptsTableData
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|attemptsTableData
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
literal|"var attemptsTableData="
operator|+
name|attemptsTableData
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
name|__
argument_list|()
expr_stmt|;
block|}
DECL|method|getAttemptId (TaskId taskId, TaskAttemptInfo ta)
specifier|protected
name|String
name|getAttemptId
parameter_list|(
name|TaskId
name|taskId
parameter_list|,
name|TaskAttemptInfo
name|ta
parameter_list|)
block|{
return|return
name|ta
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|isValidRequest ()
specifier|protected
name|boolean
name|isValidRequest
parameter_list|()
block|{
return|return
name|app
operator|.
name|getTask
argument_list|()
operator|!=
literal|null
return|;
block|}
DECL|method|getTaskAttempts ()
specifier|protected
name|Collection
argument_list|<
name|TaskAttempt
argument_list|>
name|getTaskAttempts
parameter_list|()
block|{
return|return
name|app
operator|.
name|getTask
argument_list|()
operator|.
name|getAttempts
argument_list|()
operator|.
name|values
argument_list|()
return|;
block|}
block|}
DECL|method|preHead (Page.HTML<__> html)
annotation|@
name|Override
specifier|protected
name|void
name|preHead
parameter_list|(
name|Page
operator|.
name|HTML
argument_list|<
name|__
argument_list|>
name|html
parameter_list|)
block|{
name|commonPreHead
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|ACCORDION
argument_list|,
literal|"nav"
argument_list|)
argument_list|,
literal|"{autoHeight:false, active:3}"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|DATATABLES_ID
argument_list|,
literal|"attempts"
argument_list|)
expr_stmt|;
name|set
argument_list|(
name|initID
argument_list|(
name|DATATABLES
argument_list|,
literal|"attempts"
argument_list|)
argument_list|,
name|attemptsTableInit
argument_list|()
argument_list|)
expr_stmt|;
name|setTableStyles
argument_list|(
name|html
argument_list|,
literal|"attempts"
argument_list|)
expr_stmt|;
block|}
DECL|method|content ()
annotation|@
name|Override
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|SubView
argument_list|>
name|content
parameter_list|()
block|{
return|return
name|AttemptsBlock
operator|.
name|class
return|;
block|}
DECL|method|attemptsTableInit ()
specifier|private
name|String
name|attemptsTableInit
parameter_list|()
block|{
return|return
name|tableInit
argument_list|()
operator|.
name|append
argument_list|(
literal|", 'aaData': attemptsTableData"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bDeferRender: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|", bProcessing: true"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n,aoColumnDefs:[\n"
argument_list|)
comment|//logs column should not filterable (it includes container ID which may pollute searches)
operator|.
name|append
argument_list|(
literal|"\n{'aTargets': [ 5 ]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'bSearchable': false }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'natural', 'aTargets': [ 0 ]"
argument_list|)
operator|.
name|append
argument_list|(
literal|", 'mRender': parseHadoopID }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': [ 6, 7"
argument_list|)
operator|.
name|append
argument_list|(
literal|" ], 'mRender': renderHadoopDate }"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n, {'sType':'numeric', 'aTargets': [ 8"
argument_list|)
operator|.
name|append
argument_list|(
literal|" ], 'mRender': renderHadoopElapsedTime }]"
argument_list|)
comment|// Sort by id upon page load
operator|.
name|append
argument_list|(
literal|"\n, aaSorting: [[0, 'asc']]"
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

