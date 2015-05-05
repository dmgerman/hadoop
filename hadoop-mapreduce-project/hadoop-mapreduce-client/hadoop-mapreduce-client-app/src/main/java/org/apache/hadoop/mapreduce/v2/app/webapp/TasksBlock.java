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
name|TASK_STATE
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
name|util
operator|.
name|StringHelper
operator|.
name|join
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
name|C_PROGRESSBAR
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
name|C_PROGRESSBAR_VALUE
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
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
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|TaskInfo
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
name|hamlet
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
name|hamlet
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
name|hamlet
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
DECL|class|TasksBlock
specifier|public
class|class
name|TasksBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|app
specifier|final
name|App
name|app
decl_stmt|;
DECL|method|TasksBlock (App app)
annotation|@
name|Inject
name|TasksBlock
parameter_list|(
name|App
name|app
parameter_list|)
block|{
name|this
operator|.
name|app
operator|=
name|app
expr_stmt|;
block|}
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
name|TaskType
name|type
init|=
literal|null
decl_stmt|;
name|String
name|symbol
init|=
name|$
argument_list|(
name|TASK_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|symbol
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|type
operator|=
name|MRApps
operator|.
name|taskType
argument_list|(
name|symbol
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
name|html
operator|.
name|table
argument_list|(
literal|"#tasks"
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
literal|"Task"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Progress"
argument_list|)
operator|.
name|th
argument_list|(
literal|"Status"
argument_list|)
operator|.
name|th
argument_list|(
literal|"State"
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
literal|"Elapsed Time"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
name|StringBuilder
name|tasksTableData
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"[\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|Task
name|task
range|:
name|app
operator|.
name|getJob
argument_list|()
operator|.
name|getTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|!=
literal|null
operator|&&
name|task
operator|.
name|getType
argument_list|()
operator|!=
name|type
condition|)
block|{
continue|continue;
block|}
name|String
name|taskStateStr
init|=
name|$
argument_list|(
name|TASK_STATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|taskStateStr
operator|==
literal|null
operator|||
name|taskStateStr
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|taskStateStr
operator|=
literal|"ALL"
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|taskStateStr
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ALL"
argument_list|)
condition|)
block|{
try|try
block|{
comment|// get stateUI enum
name|MRApps
operator|.
name|TaskStateUI
name|stateUI
init|=
name|MRApps
operator|.
name|taskState
argument_list|(
name|taskStateStr
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|stateUI
operator|.
name|correspondsTo
argument_list|(
name|task
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
continue|continue;
comment|// not supported state, ignore
block|}
block|}
name|TaskInfo
name|info
init|=
operator|new
name|TaskInfo
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|String
name|tid
init|=
name|info
operator|.
name|getId
argument_list|()
decl_stmt|;
name|String
name|pct
init|=
name|StringUtils
operator|.
name|formatPercent
argument_list|(
name|info
operator|.
name|getProgress
argument_list|()
operator|/
literal|100
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|tasksTableData
operator|.
name|append
argument_list|(
literal|"[\"<a href='"
argument_list|)
operator|.
name|append
argument_list|(
name|url
argument_list|(
literal|"task"
argument_list|,
name|tid
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
name|tid
argument_list|)
operator|.
name|append
argument_list|(
literal|"</a>\",\""
argument_list|)
comment|//Progress bar
operator|.
name|append
argument_list|(
literal|"<br title='"
argument_list|)
operator|.
name|append
argument_list|(
name|pct
argument_list|)
operator|.
name|append
argument_list|(
literal|"'><div class='"
argument_list|)
operator|.
name|append
argument_list|(
name|C_PROGRESSBAR
argument_list|)
operator|.
name|append
argument_list|(
literal|"' title='"
argument_list|)
operator|.
name|append
argument_list|(
name|join
argument_list|(
name|pct
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"'> "
argument_list|)
operator|.
name|append
argument_list|(
literal|"<div class='"
argument_list|)
operator|.
name|append
argument_list|(
name|C_PROGRESSBAR_VALUE
argument_list|)
operator|.
name|append
argument_list|(
literal|"' style='"
argument_list|)
operator|.
name|append
argument_list|(
name|join
argument_list|(
literal|"width:"
argument_list|,
name|pct
argument_list|,
literal|'%'
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"'></div></div>\",\""
argument_list|)
operator|.
name|append
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeJavaScript
argument_list|(
name|StringEscapeUtils
operator|.
name|escapeHtml
argument_list|(
name|info
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
name|info
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
name|info
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
name|info
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
name|info
operator|.
name|getElapsedTime
argument_list|()
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
name|tasksTableData
operator|.
name|charAt
argument_list|(
name|tasksTableData
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
name|tasksTableData
operator|.
name|delete
argument_list|(
name|tasksTableData
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|tasksTableData
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|tasksTableData
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
name|_
argument_list|(
literal|"var tasksTableData="
operator|+
name|tasksTableData
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|tbody
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

